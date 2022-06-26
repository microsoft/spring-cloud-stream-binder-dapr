// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.dapr.v1.DaprGrpc;
import io.dapr.v1.DaprProtos;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;


/**
 * The {@link DaprMessageHandler} wraps a {@link DaprGrpc.DaprStub} to publish events.
 */
public class DaprMessageHandler implements MessageHandler {

	private DaprGrpc.DaprFutureStub stub;
	private String topic;
	private String pubsubName;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	/**
	 * Construct a {@link DaprMessageHandler} with {@link DaprGrpc.DaprStub}.
	 *
	 * @param stub the dapr grpc stub
	 */
	DaprMessageHandler(DaprGrpc.DaprFutureStub stub, String topic) {
		this.stub = stub;
		this.topic = topic;
	}

	@Override
	public void handleMessage(Message<?> message) {
		this.publishEvent(pubsubName, topic, message);
	}

	/**
	 * Publish event to specified topic of specified pubsub.
	 *
	 * @param pubsubName the pubsub component name
	 * @param topic	the topic
	 * @param data the data
	 */
	private void publishEvent(String pubsubName, String topic, Object data) {
		try {
			DaprProtos.PublishEventRequest request = DaprProtos.PublishEventRequest.newBuilder()
					.setTopic(topic)
					.setPubsubName(pubsubName)
					.setData(ByteString.copyFrom(OBJECT_MAPPER.writeValueAsBytes(data))).build();
			stub.publishEvent(request);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getPubsubName() {
		return pubsubName;
	}

	public void setPubsubName(String pubsubName) {
		this.pubsubName = pubsubName;
	}
}
