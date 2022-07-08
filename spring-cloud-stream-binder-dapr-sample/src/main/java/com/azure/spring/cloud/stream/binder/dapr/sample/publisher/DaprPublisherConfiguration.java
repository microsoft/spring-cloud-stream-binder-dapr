// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample.publisher;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * Publisher configuration.
 */
@Configuration
public class DaprPublisherConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaprPublisherConfiguration.class);
	private int i = 0;

	@Bean
	public Supplier<Message<String>> supply() {
		return () -> {
			LOGGER.info("Sending message, sequence " + i);
			return MessageBuilder.withPayload("Hello world, " + i++).build();
		};
	}
}
