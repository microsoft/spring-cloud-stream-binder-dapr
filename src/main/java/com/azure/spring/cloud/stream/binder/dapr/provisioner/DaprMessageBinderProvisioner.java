// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.provisioner;

import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

/**
 * The {@link DaprMessageBinderProvisioner} is responsible for the provisioning of consumer and producer destinations.
 */
public class DaprMessageBinderProvisioner implements ProvisioningProvider<ConsumerProperties, ProducerProperties> {

	@Override
	public ProducerDestination provisionProducerDestination(String name, ProducerProperties properties)
			throws ProvisioningException {
		return new DaprMessageDestination(name);
	}

	@Override
	public ConsumerDestination provisionConsumerDestination(String name, String group, ConsumerProperties properties)
			throws ProvisioningException {
		return new DaprMessageDestination(name);
	}

	final private class DaprMessageDestination implements ProducerDestination, ConsumerDestination {

		private final String destination;

		private DaprMessageDestination(final String destination) {
			this.destination = destination;
		}

		@Override
		public String getName() {
			return destination.trim();
		}

		@Override
		public String getNameForPartition(int partition) {
			throw new UnsupportedOperationException("Partitioning is not implemented for file messaging.");
		}

	}

}
