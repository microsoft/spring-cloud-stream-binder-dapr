// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.google.protobuf.Empty;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * {@link DaprMessageHandler} that provides the logic required to publish event.
 * It delegates to {@link DaprGrpc.DaprStub} to publish event.
 */
public class DaprMessageHandler extends AbstractMessageProducingHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaprMessageHandler.class);
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
	 */
	private void publishEvent(Message<?> message) {
		DaprProtos.PublishEventRequest.Builder builder = daprMessageConverter.fromMessage(message);
		builder.setTopic(topic);
		builder.setPubsubName(pubsubName);
		daprStub.publishEvent(builder.build(), createDaprStreamObserver());
	}

	private StreamObserver<Empty> createDaprStreamObserver() {
		return new StreamObserver<Empty>() {
			@Override
			public void onNext(Empty empty) {
			}

			@Override
			public void onError(Throwable throwable) {
				LOGGER.error("Failed to publish event " + throwable.fillInStackTrace());
			}

			@Override
			public void onCompleted() {
				LOGGER.info("Success to publish event");
			}
		};
	}
}
