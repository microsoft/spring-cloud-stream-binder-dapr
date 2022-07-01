// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * The Dapr binding configuration properties.
 */
public class DaprBindingProperties implements BinderSpecificPropertiesProvider {

	private DaprConsumerProperties consumer = new DaprConsumerProperties();

	private DaprProducerProperties producer = new DaprProducerProperties();

	/**
	 * Get the consumer properties.
	 *
	 * @return consumer the consumer properties
	 */
	public DaprConsumerProperties getConsumer() {
		return consumer;
	}

	/**
	 * Set the consumer properties.
	 *
	 * @param consumer the consumer properties
	 */
	public void setConsumer(DaprConsumerProperties consumer) {
		this.consumer = consumer;
	}

	/**
	 * Get the producer properties.
	 *
	 * @return producer the producer properties
	 */
	public DaprProducerProperties getProducer() {
		return producer;
	}

	/**
	 * Set the producer properties.
	 *
	 * @param producer the producer properties
	 */
	public void setProducer(DaprProducerProperties producer) {
		this.producer = producer;
	}
}
