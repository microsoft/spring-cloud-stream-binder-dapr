// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

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
	O fromMessage(Message<?> message);

	/**
	 * Create a {@link Message} whose payload is the result of converting the given
	 * payload Object to serialized form. The optional {@link MessageHeaders} parameter
	 * may contain additional headers to be added to the message.
	 * @param daprMessage the Object to convert
	 * @param headers optional headers for the message
	 * @param targetPayloadClass the target payload class for the conversion
	 * @param <U> payload class type in message
	 * @return the new message, or {@code null} if the converter does not support the
	 * Object type or the target media type
	 */
	@Nullable
	<U> Message<U> toMessage(I daprMessage, Map<String, Object> headers, Class<U> targetPayloadClass);
}
