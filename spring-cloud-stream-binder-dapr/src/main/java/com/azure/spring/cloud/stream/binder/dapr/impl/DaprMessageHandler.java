// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import java.util.function.Consumer;

import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.google.protobuf.Empty;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * The {@link DaprMessageHandler} wraps a {@link DaprGrpc.DaprStub} to publish events.
 */
public class DaprMessageHandler extends AbstractMessageProducingHandler {
	private final DaprGrpc.DaprStub daprStub;
	private final String topic;
	private final String pubsubName;
	private final DaprMessageConverter daprMessageConverter;

	/**
	 * Construct a {@link DaprMessageHandler} with the specified {@link DaprGrpc.DaprStub}、topic and pubsubName.
	 *
	 * @param topic the topic
	 * @param pubsubName the pubsub name
	 * @param daprStub the Dapr stub
	 * @param daprMessageConverter the dapr message converter
	 */
	public DaprMessageHandler(String topic, String pubsubName, DaprGrpc.DaprStub daprStub, DaprMessageConverter daprMessageConverter) {
		Assert.hasText(topic, "topic can't be null or empty");
		Assert.hasText(pubsubName, "pubsubName can't be null or empty");
		this.topic = topic;
		this.pubsubName = pubsubName;
		this.daprStub = daprStub;
		this.daprMessageConverter = daprMessageConverter;
	}

	@Override
	protected void handleMessageInternal(Message<?> message) {
		publishEvent(message);
	}

	/**
	 * Publish event with specified message.
	 *
	 * @param message the message
	 */
	private Mono<Empty> publishEvent(Message<?> message) {
		DaprProtos.PublishEventRequest.Builder builder = daprMessageConverter.fromMessage(message);
		builder.setTopic(topic);
		builder.setPubsubName(pubsubName);
		return createMono(it -> daprStub.publishEvent(builder.build(), it));
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
}
