// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import java.util.function.Consumer;

import io.dapr.v1.DaprAppCallbackProtos;

public class DaprMessageConsumer implements Consumer<DaprAppCallbackProtos.TopicEventRequest> {
	private final String topic;
	private final String pubsubName;
	private Consumer<DaprAppCallbackProtos.TopicEventRequest> integrationConsumer;
	public DaprMessageConsumer(String pubsubName, String topic, Consumer<DaprAppCallbackProtos.TopicEventRequest> integrationConsumer) {
		this.pubsubName = pubsubName;
		this.topic = topic;
		this.integrationConsumer = integrationConsumer;
	}
	@Override
	public void accept(DaprAppCallbackProtos.TopicEventRequest request) {
		if (this.topic.equals(request.getTopic()) && this.pubsubName.equals(request.getPubsubName())) {
			integrationConsumer.accept(request);
		}
	}
}
