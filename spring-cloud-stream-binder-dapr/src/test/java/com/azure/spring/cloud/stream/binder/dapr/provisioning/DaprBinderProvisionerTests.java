// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.provisioning;

import com.azure.spring.cloud.stream.binder.dapr.properties.DaprConsumerProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprProducerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DaprBinderProvisionerTests {

	private DaprBinderProvisioner provisioner;
	private DaprProducerProperties producerProperties;
	private DaprConsumerProperties consumerProperties;

	@BeforeEach
	void beforeEach() {
		provisioner = spy(DaprBinderProvisioner.class);
		producerProperties = new DaprProducerProperties();
		consumerProperties = new DaprConsumerProperties();
	}

	@Test
	void provisionProducerDestination() {
		ExtendedProducerProperties<DaprProducerProperties> extendedProperties =
				new ExtendedProducerProperties<>(producerProperties);
		provisioner.provisionProducerDestination("test", extendedProperties);
		verify(provisioner, times(1)).validateOrCreateForProducer("test");
	}

	@Test
	void provisionConsumerDestination() {
		ExtendedConsumerProperties<DaprConsumerProperties> extendedProperties =
				new ExtendedConsumerProperties<>(consumerProperties);
		provisioner.provisionConsumerDestination("test", "group", extendedProperties);
		verify(provisioner, times(1)).validateOrCreateForConsumer("test", "group");
	}
}
