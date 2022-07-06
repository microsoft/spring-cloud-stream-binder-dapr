// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.util.HashMap;
import java.util.Map;

import io.dapr.v1.DaprProtos;
import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import static org.assertj.core.api.Assertions.assertThat;

public class DaprMessageConverterTests {

	@Test
	public void testConvertCustomHeadersToDaprRequestBuilder() {
		Map<String, Object> headerMap = new HashMap<>();
		Map<String, String> brokerMetadata = new HashMap<>();
		brokerMetadata.put("partitionKey", "fake-partiton-key");
		headerMap.put(DaprHeaders.CONTENT_TYPE, "fake-content-type");
		headerMap.put(DaprHeaders.RAW_PAY_LOAD, true);
		headerMap.put(DaprHeaders.TTL_IN_SECONDS, 20);
		headerMap.put(DaprHeaders.SPECIFIED_BROKER_METADATA, brokerMetadata);
		MessageHeaders headers = new MessageHeaders(headerMap);
		Message message = new Message() {
			@Override
			public Object getPayload() {
				return null;
			}

			@Override
			public MessageHeaders getHeaders() {
				return headers;
			}
		};
		DaprMessageConverter converter = new DaprMessageConverter();
		DaprProtos.PublishEventRequest.Builder builder = converter.fromMessage(message);
		assertThat(builder.getMetadataCount()).isEqualTo(3);
		assertThat(builder.getDataContentType()).isEqualTo("fake-content-type");
		assertThat(builder.getMetadataOrThrow(DaprHeaders.RAW_PAY_LOAD)).isEqualTo("true");
		assertThat(builder.getMetadataOrThrow(DaprHeaders.TTL_IN_SECONDS)).isEqualTo("20");
		assertThat(builder.getMetadataOrThrow("partitionKey")).isEqualTo("fake-partiton-key");
	}

	@Test
	public void testUnsupportHeadersToDaprRequestBuilder() {
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("fake-header", "fake-value");
		MessageHeaders headers = new MessageHeaders(headerMap);
		Message message = new Message() {
			@Override
			public Object getPayload() {
				return null;
			}

			@Override
			public MessageHeaders getHeaders() {
				return headers;
			}
		};
		DaprMessageConverter converter = new DaprMessageConverter();
		DaprProtos.PublishEventRequest.Builder builder = converter.fromMessage(message);
		assertThat(builder.getMetadataCount()).isEqualTo(0);
		assertThat(builder.getMetadataOrDefault("fake-header", "NULL")).isEqualTo("NULL");
	}
}
