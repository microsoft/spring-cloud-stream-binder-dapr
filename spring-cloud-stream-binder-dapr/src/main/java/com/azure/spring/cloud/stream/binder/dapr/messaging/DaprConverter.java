// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * A converter to turn a {@link Message} from serialized form to a typed Object and vice versa.
 *
 * @param <I> The Dapr message type when sending to the broker using Dapr SDK.
 * @param <O> The Dapr message type when receiving from the broker using Dapr SDK.
 */
public interface DaprConverter<I, O> {

	/**
	 * Convert a {@link Message} from a serialized form to a typed Object.
	 *
	 * @param message the input message
	 * @return the result of the conversion, or {@code null} if the converter cannot perform the conversion.
	 */
	@Nullable
	I fromMessage(Message<?> message) throws JsonProcessingException;

	/**
	 * Create a {@link Message} whose payload is the result of converting the given
	 * payload Object to serialized form.
	 * @param daprMessage the Object to convert
	 * @return the new message, or {@code null} if the converter does not support the
	 * Object type or the target media type
	 */
	@Nullable
	Message toMessage(O daprMessage) throws IOException;
}
