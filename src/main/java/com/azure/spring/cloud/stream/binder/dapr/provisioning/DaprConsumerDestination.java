// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.provisioning;

import org.springframework.cloud.stream.provisioning.ConsumerDestination;

/**
 * The Dapr {@link ConsumerDestination} implementation.
 */
public class DaprConsumerDestination implements ConsumerDestination {

	private final String topic;

	/**
	 * Construct a {@link DaprConsumerDestination} with the specified binder.
	 *
	 * @param topic the topic
	 */
	public DaprConsumerDestination(final String topic) {
		this.topic = topic;
	}

	@Override
	public String getName() {
		return topic.trim();
	}
}
