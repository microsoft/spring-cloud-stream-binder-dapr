// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprBinderProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import io.dapr.v1.DaprGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The auto-configuration for Dapr binder.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties({DaprBinderProperties.class, DaprExtendedBindingProperties.class})
public class DaprBinderConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DaprBinderProvisioner daprBinderProvisioner() {
		return new DaprBinderProvisioner();
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageConverter daprMessageConverter() {
		return new DaprMessageConverter();
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprGrpc.DaprStub daprStub(DaprBinderProperties daprBinderProperties) {
		ManagedChannel managedChannel = ManagedChannelBuilder
				.forAddress(daprBinderProperties.getDaprIp(), daprBinderProperties.getDaprPort())
				.usePlaintext()
				.build();
		return DaprGrpc.newStub(managedChannel);
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageChannelBinder daprMessageChannelBinder(DaprBinderProvisioner daprBinderProvisioner,
			DaprExtendedBindingProperties daprExtendedBindingProperties,
			DaprGrpc.DaprStub daprStub,
			DaprMessageConverter daprMessageConverter) {
		return new DaprMessageChannelBinder(
				null,
				daprBinderProvisioner,
				daprStub,
				daprExtendedBindingProperties,
				daprMessageConverter);
	}
}
