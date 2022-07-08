// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.dapr.v1.DaprAppCallbackProtos;
import io.dapr.v1.DaprProtos;

import org.springframework.cloud.stream.converter.ConversionException;
import org.springframework.messaging.Message;

/**
 * A converter to turn a {@link Message} to {@link DaprProtos.PublishEventRequest.Builder}.
 * and turn a {@link DaprAppCallbackProtos.TopicEventRequest} to {@link Message}.
 */
public class DaprMessageConverter implements DaprConverter<DaprAppCallbackProtos.TopicEventRequest,
		DaprProtos.PublishEventRequest.Builder> {
	private static final Set<String> SUPPORT_MESSAGE_HEADERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			DaprHeaders.RAW_PAYLOAD,
			DaprHeaders.TTL_IN_SECONDS
	)));
	private final ObjectMapper objectMapper;

	public DaprMessageConverter() {
		objectMapper = new ObjectMapper();
	}

	@Override
	public DaprProtos.PublishEventRequest.Builder fromMessage(Message<?> message) {
		Map<String, Object> copyHeaders  = new HashMap(message.getHeaders());
		DaprProtos.PublishEventRequest.Builder builder = DaprProtos.PublishEventRequest.newBuilder();
		try {
			builder.setData(ByteString.copyFrom(objectMapper.writeValueAsBytes(message)));
		}
		catch (JsonProcessingException e) {
			throw new ConversionException("Failed to convert Spring message to Dapr PublishEventRequest Builder data:" + e);
		}
		String contentType = (String) copyHeaders.get(DaprHeaders.CONTENT_TYPE);
		if (contentType != null) {
			builder.setDataContentType(contentType);
		}
		copyHeaders.remove(DaprHeaders.CONTENT_TYPE);
		Map<String, String> brokerMetadatas = (Map<String, String>) copyHeaders.get(DaprHeaders.SPECIFIED_BROKER_METADATA);
		if (brokerMetadatas != null && !brokerMetadatas.isEmpty()) {
			builder.putAllMetadata(brokerMetadatas);
		}
		copyHeaders.remove(DaprHeaders.SPECIFIED_BROKER_METADATA);
		SUPPORT_MESSAGE_HEADERS.forEach(key -> {
			Object value = copyHeaders.get(key);
			if (value != null) {
				builder.putMetadata(key, value.toString());
				copyHeaders.remove(key);
			}
		});
		return builder;
	}

	@Override
	public <U> Message<U> toMessage(DaprAppCallbackProtos.TopicEventRequest daprMessage,
			Map<String, Object> headers, Class<U> targetPayloadClass) {
		return null;
	}
}
