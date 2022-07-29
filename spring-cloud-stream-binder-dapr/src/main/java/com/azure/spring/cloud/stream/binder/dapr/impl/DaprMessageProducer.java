// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.azure.spring.cloud.stream.binder.dapr.service.DaprGrpcService;
import io.dapr.v1.DaprAppCallbackProtos;

import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.messaging.Message;

/**
 * The {@link DaprMessageProducer} is responsible for consuming the events.
 */
public class DaprMessageProducer extends MessageProducerSupport {
	private final DaprGrpcService daprGrpcService;
	private final DaprMessageConverter daprMessageConverter;
	public DaprMessageProducer(DaprGrpcService daprGrpcService,
			DaprMessageConverter daprMessageConverter,
			String pubsubName,
			String topic) {
		this.daprMessageConverter = daprMessageConverter;
		this.daprGrpcService = daprGrpcService;
		this.daprGrpcService.registerConsumer(pubsubName, topic, new DaprMessageConsumer(pubsubName, topic, this::onMessage));
	}

	private void onMessage(DaprAppCallbackProtos.TopicEventRequest request) {
		Message message = daprMessageConverter.toMessage(request);
		sendMessage(message);
	}
}
