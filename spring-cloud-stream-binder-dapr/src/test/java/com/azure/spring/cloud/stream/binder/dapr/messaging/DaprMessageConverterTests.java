// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.util.HashMap;
import java.util.Map;

import io.dapr.v1.DaprProtos;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DaprMessageConverterTests {

	@Test
	public void testConvertCustomHeadersToDaprRequestBuilder() {
		Map<String, String> brokerMetadata = new HashMap<>();
		brokerMetadata.put("partitionKey", "fake-partiton-key");
		Message<?> message = MessageBuilder.withPayload("testPayload".getBytes())
				.setHeader(DaprHeaders.CONTENT_TYPE, "fake-content-type")
				.setHeader(DaprHeaders.RAW_PAYLOAD, true)
				.setHeader(DaprHeaders.TTL_IN_SECONDS, 20)
				.setHeader(DaprHeaders.SPECIFIED_BROKER_METADATA, brokerMetadata).build();
		DaprMessageConverter converter = new DaprMessageConverter();
		DaprProtos.PublishEventRequest.Builder builder = converter.fromMessage(message);
		assertThat(builder.getMetadataCount()).isEqualTo(3);
		assertThat(builder.getDataContentType()).isEqualTo("fake-content-type");
		assertThat(builder.getMetadataOrThrow(DaprHeaders.RAW_PAYLOAD)).isEqualTo("true");
		assertThat(builder.getMetadataOrThrow(DaprHeaders.TTL_IN_SECONDS)).isEqualTo("20");
		assertThat(builder.getMetadataOrThrow("partitionKey")).isEqualTo("fake-partiton-key");
	}

	@Test
	public void testUnsupportHeadersToDaprRequestBuilder() {
		Message<?> message = MessageBuilder.withPayload("testPayload".getBytes())
				.setHeader("fake-header", "fake-value").build();
		DaprMessageConverter converter = new DaprMessageConverter();
		DaprProtos.PublishEventRequest.Builder builder = converter.fromMessage(message);
		assertThat(builder.getMetadataCount()).isEqualTo(0);
		catchThrowable(() -> builder.getMetadataOrThrow("fake-header"));
	}
}
