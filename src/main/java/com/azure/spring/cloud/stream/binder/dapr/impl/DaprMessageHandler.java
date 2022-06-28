// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.util.Assert;

/**
 * The {@link DaprMessageHandler} wraps a {@link DaprGrpc.DaprStub} to publish events.
 */
public class DaprMessageHandler extends AbstractMessageProducingHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaprMessageHandler.class);
	private Expression sendTimeoutExpression = new ValueExpression(10000L);
	private boolean sync = false;
	private DaprGrpc.DaprStub daprStub;
	private String topic;
	private String pubsubName;
	private EvaluationContext evaluationContext;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	/**
	 * Construct a {@link DaprMessageHandler} with {@link DaprGrpc.DaprStub}.
	 *
	 * @param topic the topic
	 * @param pubsubName the pubsubName
	 * @param daprStub the dapr stub
	 */
	public DaprMessageHandler(String topic, String pubsubName, DaprGrpc.DaprStub daprStub) {
		Assert.hasText(topic, "topic can't be null or empty");
		Assert.hasText(pubsubName, "pubsubName can't be null or empty");
		this.topic = topic;
		this.pubsubName = pubsubName;
		this.daprStub = daprStub;
	}

	@Override
	protected void onInit() {
		super.onInit();
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(this.getBeanFactory());
	}

	@Override
	protected void handleMessageInternal(Message<?> message) {
		Mono<Empty> mono = publishEvent(pubsubName, topic, message);
		if (this.sync) {
			this.waitingSendResponse(mono, message);
		}
		else {
			this.handleSendResponseAsync(mono, message);
		}
	}

	private <T> void waitingSendResponse(Mono<T> mono, Message<?> message) {
		Long sendTimeout = this.sendTimeoutExpression.getValue(this.evaluationContext, message, Long.class);
		if (sendTimeout != null && sendTimeout >= 0L) {
			try {
				mono.block(Duration.of(sendTimeout, ChronoUnit.MILLIS));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} sent successfully in sync mode", message);
				}
			}
			catch (Exception var6) {
				if (var6.getCause() instanceof TimeoutException) {
					throw new MessageTimeoutException(message, "Timeout waiting for send dapr response");
				}
				throw new MessageDeliveryException(var6.getMessage());
			}
		}
		else {
			try {
				mono.block();
			}
			catch (Exception var5) {
				throw new MessageDeliveryException(var5.getMessage());
			}
		}
	}

	private <T> void handleSendResponseAsync(Mono<T> mono, Message<?> message) {
		mono.doOnError((ex) -> {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("{} sent failed in async mode due to {}", message, ex.getMessage());
			}
		}).doOnSuccess((t) -> {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} sent successfully in async mode", message);
			}
		}).subscribe();
	}

	/**
	 * Publish event to specified topic of specified pubsub.
	 *
	 * @param pubsubName the pubsub component name
	 * @param topic	the topic
	 * @param data the data
	 */
	private Mono<Empty> publishEvent(String pubsubName, String topic, Object data) {
		try {
			DaprProtos.PublishEventRequest request = DaprProtos.PublishEventRequest.newBuilder()
					.setTopic(topic)
					.setPubsubName(pubsubName)
					.setData(ByteString.copyFrom(OBJECT_MAPPER.writeValueAsBytes(data))).build();
			return this.<Empty>createMono(it -> {
				daprStub.publishEvent(request, it);
			});
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private <T> Mono<T> createMono(Consumer<StreamObserver<T>> consumer) {
		return Mono.create(sink -> consumer.accept(createStreamObserver(sink)));
	}

	private <T> StreamObserver<T> createStreamObserver(MonoSink<T> sink) {
		return new StreamObserver<T>() {
			@Override
			public void onNext(T value) {
				sink.success(value);
			}

			@Override
			public void onError(Throwable t) {
				sink.error(t);
			}

			@Override
			public void onCompleted() {
				sink.success();
			}
		};
	}

	/**
	 * Get sync.
	 *
	 * @return sync the sync
	 */
	public boolean isSync() {
		return sync;
	}

	/**
	 * Set sync.
	 *
	 * @param sync the sync
	 */
	public void setSync(boolean sync) {
		this.sync = sync;
		LOGGER.info("DaprMessageHandler sync becomes: {}", sync);
	}

	/**
	 * Set send timeout expression.
	 *
	 * @param sendTimeoutExpression the send timeout expression
	 */
	public void setSendTimeoutExpression(Expression sendTimeoutExpression) {
		Assert.notNull(sendTimeoutExpression, "'sendTimeoutExpression' must not be null");
		this.sendTimeoutExpression = sendTimeoutExpression;
		LOGGER.info("DaprMessageHandler syncTimeout becomes: {}", sendTimeoutExpression);
	}

	/**
	 * Set send timeout.
	 *
	 * @param sendTimeout the send timeout
	 */
	public void setSendTimeout(long sendTimeout) {
		this.setSendTimeoutExpression(new ValueExpression(sendTimeout));
	}

	/**
	 * Get send time out expression.
	 *
	 * @return sendTimeoutExpression the send time out expression
	 */
	public Expression getSendTimeoutExpression() {
		return this.sendTimeoutExpression;
	}
}
