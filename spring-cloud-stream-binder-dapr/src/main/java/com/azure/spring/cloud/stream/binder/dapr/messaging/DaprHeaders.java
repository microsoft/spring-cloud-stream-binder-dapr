// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.messaging;

/**
 * Dapr internal headers for spring messaging messages.
 */
public final class DaprHeaders {

	private DaprHeaders() {
	}

	/**
	 * The contentType tells Dapr which content type your data adheres to when constructing a CloudEvent envelope.
	 *
	 * Type: String
	 */
	public static final String CONTENT_TYPE = "contentType";

	/**
	 * The number of seconds for the message to expire.
	 *
	 * Type: Long
	 */
	public static final String TTL_IN_SECONDS = "ttlInSeconds";

	/**
	 * Determine if Dapr should publish the event without wrapping it as CloudEvent.Not using CloudEvents disables support for tracing, event deduplication per messageId, content-type metadata, and any other features built using the CloudEvent schema.
	 *
	 * Type: Boolean
	 */
	public static final String RAW_PAY_LOAD = "rawPayLoad";


	/**
	 * Some metadata parameters are available based on each pubsub broker component.
	 * You could refer <a href="https://docs.dapr.io/reference/components-reference/supported-pubsub/setup-apache-kafka/#per-call-metadata-fields">Kafka per-call metadata fields</a>.
	 *
	 * Type: Map
	 * Key Type: String
	 * Value Type: String
	 */
	public static final String SPECIFIED_BROKER_METADATA = "specifiedBrokerMetadata";
}
