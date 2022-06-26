// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.provisioning;

import org.springframework.cloud.stream.provisioning.ProducerDestination;

/**
 * The Dapr {@link ProducerDestination} implementation.
 */
public class DaprProducerDestination implements ProducerDestination {

	private final String topic;

	/**
	 * Construct a {@link DaprProducerDestination} with the specified binder.
	 *
	 * @param topic the topic
	 */
	public DaprProducerDestination(String topic) {
		this.topic = topic;
	}

	@Override
	public String getName() {
		return topic.trim();
	}

	@Override
	public String getNameForPartition(int partition) {
		return topic + "-" + partition;
	}
}
