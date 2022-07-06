// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.config.DaprBinderConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class DaprExtendedBindingPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DaprBinderConfiguration.class));

	@Test
	void testExtendedBindingPropertiesShouldBind() {
		this.contextRunner
			.withPropertyValues(
					"spring.cloud.stream.dapr.bindings.input.producer.pubsub-name=fake-pubsub-name",
					"spring.cloud.stream.dapr.binder.dapr-ip=127.0.0.2",
					"spring.cloud.stream.dapr.binder.dapr-port=50000"
			)
			.run(context -> {
				assertThat(context).hasSingleBean(DaprMessageChannelBinder.class);
				DaprMessageChannelBinder binder = context.getBean(DaprMessageChannelBinder.class);
				DaprProducerProperties producerProperties =
						binder.getExtendedProducerProperties("input");
				assertThat(producerProperties.getPubsubName()).isEqualTo("fake-pubsub-name");
				DaprBinderProperties binderProperties = context.getBean(DaprBinderProperties.class);
				assertThat(binderProperties.getDaprPort()).isEqualTo(50000);
				assertThat(binderProperties.getDaprIp()).isEqualTo("127.0.0.2");
			});
	}
}
