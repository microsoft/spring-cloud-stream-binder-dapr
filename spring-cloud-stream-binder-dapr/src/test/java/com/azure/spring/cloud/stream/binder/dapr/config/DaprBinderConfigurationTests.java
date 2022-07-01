// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.stream.binder.Binder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DaprBinderConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DaprBinderConfiguration.class));

	@Test
	void testConfigurationNotMatchedWhenBinderBeanExist() {
		this.contextRunner
				.withBean(Binder.class, () -> mock(Binder.class))
				.run(context -> {
					assertThat(context).doesNotHaveBean(DaprBinderConfiguration.class);
					assertThat(context).doesNotHaveBean(DaprMessageChannelBinder.class);
				});
	}

	@Test
	void testDefaultConfiguration() {
		this.contextRunner
				.run(context -> {
					assertThat(context).hasSingleBean(DaprBinderConfiguration.class);
					assertThat(context).hasSingleBean(DaprExtendedBindingProperties.class);
					assertThat(context).hasSingleBean(DaprBinderProvisioner.class);
					assertThat(context).hasSingleBean(DaprMessageChannelBinder.class);
				});
	}
}
