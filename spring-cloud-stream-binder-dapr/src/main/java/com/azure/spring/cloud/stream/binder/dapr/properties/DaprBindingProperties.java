// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * The Dapr binding configuration properties.
 */
public class DaprBindingProperties implements BinderSpecificPropertiesProvider {

	/**
	 * The Dapr consumer binding configuration properties.
	 */
	private DaprConsumerProperties consumer = new DaprConsumerProperties();

	/**
	 * The Dapr producer binding configuration properties.
	 */
	private DaprProducerProperties producer = new DaprProducerProperties();

	public DaprConsumerProperties getConsumer() {
		return consumer;
	}

	public void setConsumer(DaprConsumerProperties consumer) {
		this.consumer = consumer;
	}

	public DaprProducerProperties getProducer() {
		return producer;
	}

	public void setProducer(DaprProducerProperties producer) {
		this.producer = producer;
	}
}
