// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import java.util.HashMap;
import java.util.Map;

import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DaprMessageHandlerTests {

	@Test
	public void testSend() {
		String topic = "topic";
		String pubsubName = "pubsub";
		Map<String, Object> valueMap = new HashMap<>(2);
		valueMap.put("key1", "value1");
		valueMap.put("key2", "value2");

		Message<?> message = new GenericMessage<>("testPayload".getBytes(), valueMap);
		DaprMessageConverter daprMessageConverter = new DaprMessageConverter();
		DaprGrpc.DaprStub daprStub = mock(DaprGrpc.DaprStub.class);
		DaprMessageHandler daprMessageHandler = new DaprMessageHandler(topic, pubsubName, daprStub, daprMessageConverter);

		daprMessageHandler.handleMessage(message);
		verify(daprStub, times(1)).publishEvent(isA(DaprProtos.PublishEventRequest.class), isA(StreamObserver.class));
	}
}
