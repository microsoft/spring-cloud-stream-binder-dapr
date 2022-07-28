// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import com.azure.spring.cloud.stream.binder.dapr.service.DaprGrpcService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DaprServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DaprGrpcService daprGrpcService() {
		return new DaprGrpcService();
	}
}
