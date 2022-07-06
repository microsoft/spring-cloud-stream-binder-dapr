// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DaprProducerPropertiesTests {

	private DaprProducerProperties daprProducerProperties;

	@BeforeEach
	void beforeEach() {
		daprProducerProperties = new DaprProducerProperties();
	}

	@Test
	void customPubsubName() {
		String pubsubName = "pubsub";
		daprProducerProperties.setPubsubName(pubsubName);
		assertThat(daprProducerProperties.getPubsubName()).isEqualTo(pubsubName);
	}
}
