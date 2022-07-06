// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import org.springframework.messaging.Message;

/**
 * Dapr converter interface.
 */
public interface DaprConverter<T> {

	/**
	 * Convert spring message to specified traget class.
	 * @param message the input message.
	 *
	 * @return the result of the conversion
	 */
	T fromMessage(Message<?> message);
}
