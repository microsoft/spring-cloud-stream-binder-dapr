// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprBinderConfigurationProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprProducerProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import com.azure.spring.cloud.stream.binder.dapr.service.DaprGrpcService;
import io.dapr.v1.DaprGrpc;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.stream.binder.Binder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DaprBinderConfigurationTests {

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
				.withBean(DaprGrpcService.class, () -> mock(DaprGrpcService.class))
				.run(context -> {
					assertThat(context).hasSingleBean(DaprBinderConfiguration.class);
					assertThat(context).hasSingleBean(DaprExtendedBindingProperties.class);
					assertThat(context).hasSingleBean(DaprBinderProvisioner.class);
					assertThat(context).hasSingleBean(DaprMessageChannelBinder.class);
					assertThat(context).hasSingleBean(DaprBinderConfigurationProperties.class);
					assertThat(context).hasSingleBean(DaprGrpc.DaprStub.class);
					assertThat(context).hasSingleBean(DaprGrpcService.class);
				});
	}

	@Test
	void testExtendedBindingPropertiesShouldBind() {
		this.contextRunner
				.withBean(DaprGrpcService.class, () -> mock(DaprGrpcService.class))
				.withPropertyValues(
						"spring.cloud.stream.dapr.bindings.input.producer.pubsub-name=fake-pubsub-name",

						"spring.cloud.stream.dapr.binder.dapr-ip=127.0.0.2",
						"spring.cloud.stream.dapr.binder.dapr-port=50000",

						"spring.cloud.stream.dapr.binder.managed-channel.defaultLoadBalancingPolicy=fake-policy",
						"spring.cloud.stream.dapr.binder.managed-channel.authority=fake-authority",
						"spring.cloud.stream.dapr.binder.managed-channel.idleTimeout=20",
						"spring.cloud.stream.dapr.binder.managed-channel.keepAliveTime=30",
						"spring.cloud.stream.dapr.binder.managed-channel.keepAliveTimeout=40",
						"spring.cloud.stream.dapr.binder.managed-channel.perRpcBufferLimit=50",
						"spring.cloud.stream.dapr.binder.managed-channel.retryBufferSize=60",
						"spring.cloud.stream.dapr.binder.managed-channel.maxHedgedAttempts=2000",
						"spring.cloud.stream.dapr.binder.managed-channel.maxInboundMessageSize=3000",
						"spring.cloud.stream.dapr.binder.managed-channel.maxInboundMetadataSize=4000",
						"spring.cloud.stream.dapr.binder.managed-channel.maxRetryAttempts=5000",
						"spring.cloud.stream.dapr.binder.managed-channel.maxTraceEvents=6000",
						"spring.cloud.stream.dapr.binder.managed-channel.keepAliveWithoutCalls=true",
						"spring.cloud.stream.dapr.binder.managed-channel.negotiationType=TLS",

						"spring.cloud.stream.dapr.binder.dapr-stub.maxInboundMessageSize=20",
						"spring.cloud.stream.dapr.binder.dapr-stub.maxOutboundMessageSize=20",
						"spring.cloud.stream.dapr.binder.dapr-stub.compression=fake-compression"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(DaprMessageChannelBinder.class);
					DaprMessageChannelBinder binder = context.getBean(DaprMessageChannelBinder.class);
					DaprProducerProperties producerProperties =
							binder.getExtendedProducerProperties("input");
					assertThat(producerProperties.getPubsubName()).isEqualTo("fake-pubsub-name");
					DaprBinderConfigurationProperties binderProperties = context.getBean(DaprBinderConfigurationProperties.class);
					assertThat(binderProperties.getDaprPort()).isEqualTo(50000);
					assertThat(binderProperties.getDaprIp()).isEqualTo("127.0.0.2");

					DaprBinderConfigurationProperties.ManagedChannel managedChannel = binderProperties.getManagedChannel();
					assertThat(managedChannel.getDefaultLoadBalancingPolicy()).isEqualTo("fake-policy");
					assertThat(managedChannel.getAuthority()).isEqualTo("fake-authority");
					assertThat(managedChannel.getIdleTimeout()).isEqualTo(20);
					assertThat(managedChannel.getKeepAliveTime()).isEqualTo(30);
					assertThat(managedChannel.getKeepAliveTimeout()).isEqualTo(40);
					assertThat(managedChannel.getPerRpcBufferLimit()).isEqualTo(50);
					assertThat(managedChannel.getRetryBufferSize()).isEqualTo(60);
					assertThat(managedChannel.getMaxHedgedAttempts()).isEqualTo(2000);
					assertThat(managedChannel.getMaxInboundMessageSize()).isEqualTo(3000);
					assertThat(managedChannel.getMaxInboundMetadataSize()).isEqualTo(4000);
					assertThat(managedChannel.getMaxRetryAttempts()).isEqualTo(5000);
					assertThat(managedChannel.getMaxTraceEvents()).isEqualTo(6000);
					assertThat(managedChannel.isKeepAliveWithoutCalls()).isEqualTo(true);
					assertThat(managedChannel.getNegotiationType()).isEqualTo(DaprBinderConfigurationProperties.NegotiationType.TLS);

					DaprBinderConfigurationProperties.DaprStub daprStub = binderProperties.getDaprStub();
					assertThat(daprStub.getMaxInboundMessageSize()).isEqualTo(20);
					assertThat(daprStub.getMaxOutboundMessageSize()).isEqualTo(20);
					assertThat(daprStub.getCompression()).isEqualTo("fake-compression");

				});
	}
}
