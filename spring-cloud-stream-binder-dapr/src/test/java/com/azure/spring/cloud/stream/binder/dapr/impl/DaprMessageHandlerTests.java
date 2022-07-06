// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.impl;

import java.util.HashMap;
import java.util.Map;

import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class DaprMessageHandlerTests {
	private Message<?> message;
	private AutoCloseable closeable;
	protected String topic = "topic";
	protected String pubsubName = "pubsub";
	protected DaprMessageHandler handler;
	protected DaprMessageConverter daprMessageConverter;
	protected DaprGrpc.DaprStub daprStub;

	public DaprMessageHandlerTests() {
		Map<String, Object> valueMap = new HashMap<>(2);
		valueMap.put("key1", "value1");
		valueMap.put("key2", "value2");
		message = new GenericMessage<>("testPayload", valueMap);
	}

	@BeforeEach
	public void setUp() {
		this.daprMessageConverter = new DaprMessageConverter();
		this.closeable = MockitoAnnotations.openMocks(this);
		this.daprStub = mock(DaprGrpc.DaprStub.class);
		this.handler = new DaprMessageHandler(topic, pubsubName, daprStub, daprMessageConverter);
	}

	@AfterEach
	void close() throws Exception {
		closeable.close();
	}

	@Test
	public void testSend() {
		this.handler.handleMessage(this.message);
		verify(this.daprStub, times(1)).publishEvent(isA(DaprProtos.PublishEventRequest.class), isA(StreamObserver.class));
	}
}
