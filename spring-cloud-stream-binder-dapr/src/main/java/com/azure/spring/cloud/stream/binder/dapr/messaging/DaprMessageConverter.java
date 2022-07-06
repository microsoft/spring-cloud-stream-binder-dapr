// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.dapr.v1.DaprProtos;

import org.springframework.cloud.stream.converter.ConversionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * A converter to turn a {@link Message} to {@link DaprProtos.PublishEventRequest.Builder}.
 */
public class DaprMessageConverter implements DaprConverter<DaprProtos.PublishEventRequest.Builder> {
	private static final Set<String> SUPPORT_MESSAGE_HEADERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			DaprHeaders.RAW_PAY_LOAD,
			DaprHeaders.TTL_IN_SECONDS
	)));
	private final ObjectMapper objectMapper;

	public DaprMessageConverter() {
		objectMapper = new ObjectMapper();
	}

	@Override
	public DaprProtos.PublishEventRequest.Builder fromMessage(Message<?> message) {
		MessageHeaders headers = message.getHeaders();
		DaprProtos.PublishEventRequest.Builder builder = DaprProtos.PublishEventRequest.newBuilder();
		try {
			builder.setData(ByteString.copyFrom(objectMapper.writeValueAsBytes(message.getPayload())));
		}
		catch (JsonProcessingException e) {
			throw new ConversionException("Failed to write JSON: " + message, e);
		}
		String contentType = headers.get(DaprHeaders.CONTENT_TYPE, String.class);
		if (contentType != null) {
			builder.setDataContentType(contentType);
		}
		headers.forEach((key, value) -> {
			if (SUPPORT_MESSAGE_HEADERS.contains(key)) {
				builder.putMetadata(key, value.toString());
			}
		});
		Map<String, String> brokerMetadatas = headers.get(DaprHeaders.SPECIFIED_BROKER_METADATA, Map.class);
		if (brokerMetadatas != null && !brokerMetadatas.isEmpty()) {
			builder.putAllMetadata(brokerMetadatas);
		}
		return builder;
	}
}
