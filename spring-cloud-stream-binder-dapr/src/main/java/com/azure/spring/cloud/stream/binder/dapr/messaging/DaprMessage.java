// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.io.Serializable;
import java.util.Map;

import org.springframework.messaging.Message;

/**
 * Encapsulates {@link Message} payload and headers for serialization.
 */
public class DaprMessage implements Serializable {

	byte[] payload;

	Map<String, Object> headers;

	DaprMessage() {
	}

	public DaprMessage(byte[] payload, Map<String, Object> headers) {
		this.payload = payload;
		this.headers = headers;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
}
