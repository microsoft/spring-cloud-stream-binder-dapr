// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DaprBinderPropertiesTests {

	private DaprBinderProperties binderProperties;

	@BeforeEach
	void beforeEach() {
		binderProperties = new DaprBinderProperties();
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
	void customDaprIp() {
		String daprIp = "127.0.0.2";
		binderProperties.setDaprIp(daprIp);
		assertThat(binderProperties.getDaprIp()).isEqualTo(daprIp);
	}

	@Test
	void customDaprPort() {
		int daprPort = 50002;
		binderProperties.setDaprPort(daprPort);
		assertThat(binderProperties.getDaprPort()).isEqualTo(daprPort);
	}
}
