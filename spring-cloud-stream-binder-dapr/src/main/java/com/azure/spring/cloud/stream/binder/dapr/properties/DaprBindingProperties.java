// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * Container object for Dapr specific extended producer and consumer binding properties.
 */
public class DaprBindingProperties implements BinderSpecificPropertiesProvider {

	/**
	 * Consumer specific binding properties. @see {@link DaprConsumerProperties}.
	 */
	private DaprConsumerProperties consumer = new DaprConsumerProperties();

	/**
	 * Producer specific binding properties. @see {@link DaprProducerProperties}.
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
