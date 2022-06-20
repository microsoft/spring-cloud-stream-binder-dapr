/*
 * Source code recreated from a .class file by IntelliJ IDEA
 * (powered by FernFlower decompiler)
 */

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.provisioner.DaprMessageBinderProvisioner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DaprMessageBinderConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageBinderProvisioner daprMessageBinderProvisioner() {
		return new DaprMessageBinderProvisioner();
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageChannelBinder daprMessageBinder(DaprMessageBinderProvisioner daprMessageBinderProvisioner) {
		return new DaprMessageChannelBinder(null, daprMessageBinderProvisioner);
	}

}
