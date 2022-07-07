// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample.dapr;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dapr server auto configuration
 */
@EnableConfigurationProperties({ DaprServerProperties.class })
@Configuration
public class DaprServerAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
	DaprSpringService daprSpringService(DaprServerProperties clientProperties) {
        return new DaprSpringService(clientProperties);
    }
}
