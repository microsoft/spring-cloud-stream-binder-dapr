// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DaprBinderConfigurationPropertiesTests {

	private DaprBinderConfigurationProperties binderProperties;

	@BeforeEach
	void beforeEach() {
		binderProperties = new DaprBinderConfigurationProperties();
	}

	@Test
	void daprIpDefault() {
		assertThat(binderProperties.getDaprIp()).isEqualTo("127.0.0.1");
	}

	@Test
	void daprPortDefault() {
		assertThat(binderProperties.getDaprPort()).isEqualTo(50001);
	}

	@Test
	void negotiationTypeDefault() {
		DaprBinderConfigurationProperties.ManagedChannel managedChannel = binderProperties.getManagedChannel();
		assertThat(managedChannel).isNotNull();
		assertThat(managedChannel.getNegotiationType())
				.isEqualTo(DaprBinderConfigurationProperties.NegotiationType.PLAINTEXT);
	}
}
