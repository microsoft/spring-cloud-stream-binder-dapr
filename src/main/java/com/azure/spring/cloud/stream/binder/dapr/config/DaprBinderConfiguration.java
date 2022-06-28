// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The auto-configuration for dapr message binder.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties({DaprExtendedBindingProperties.class})
public class DaprBinderConfiguration {

	/**
	 * Declare {@link DaprBinderProvisioner} bean.
	 *
	 * @return the {@link DaprBinderProvisioner} bean.
	 */
	@Bean
	@ConditionalOnMissingBean
	public DaprBinderProvisioner daprBinderProvisioner() {
		return new DaprBinderProvisioner();
	}

	/**
	 * Declare {@link DaprMessageChannelBinder} bean.
	 *
	 * @param daprBinderProvisioner the dapr binder provisioner.
	 * @param daprExtendedBindingProperties the dapr extended binding properties.
	 *
	 * @return the {@link DaprMessageChannelBinder} bean.
	 */
	@Bean
	@ConditionalOnMissingBean
	public DaprMessageChannelBinder daprMessageChannelBinder(DaprBinderProvisioner daprBinderProvisioner,
			DaprExtendedBindingProperties daprExtendedBindingProperties) {
		DaprMessageChannelBinder daprMessageChannelBinder = new DaprMessageChannelBinder(null, daprBinderProvisioner);
		daprMessageChannelBinder.setBindingProperties(daprExtendedBindingProperties);
		return daprMessageChannelBinder;
	}
}
