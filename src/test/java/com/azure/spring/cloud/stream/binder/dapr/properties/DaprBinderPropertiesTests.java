// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;


import java.time.Duration;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.config.DaprBinderConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class DaprBinderPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DaprBinderConfiguration.class));

	@Test
	void testExtendedBindingPropertiesShouldBind() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.stream.dapr.bindings.input.producer.sync=true",
						"spring.cloud.stream.dapr.bindings.input.producer.pubsub-name=fake-pubsub-name",
						"spring.cloud.stream.dapr.bindings.input.producer.sidecar-ip=127.0.0.2",
						"spring.cloud.stream.dapr.bindings.input.producer.grpc-port=50000",
						"spring.cloud.stream.dapr.bindings.input.producer.send-timeout=5000",
						"spring.cloud.stream.dapr.bindings.input.producer.content-type=fake-content-type"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(DaprMessageChannelBinder.class);
					DaprMessageChannelBinder binder = context.getBean(DaprMessageChannelBinder.class);

					DaprProducerProperties producerProperties =
							binder.getExtendedProducerProperties("input");
					assertEquals("fake-pubsub-name", producerProperties.getPubsubName());
					assertEquals("fake-content-type", producerProperties.getContentType());
					assertEquals("127.0.0.2", producerProperties.getSidecarIp());
					assertTrue(producerProperties.isSync());
					assertEquals(Duration.ofMillis(5000), producerProperties.getSendTimeout());
					assertEquals(50000, producerProperties.getGrpcPort().longValue());
				});
	}
}
