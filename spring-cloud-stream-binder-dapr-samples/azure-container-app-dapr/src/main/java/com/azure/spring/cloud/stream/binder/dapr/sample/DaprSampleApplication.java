// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@SpringBootApplication
public class DaprSampleApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DaprSampleApplication.class);
	private int i = 0;
	public static void main(String[] args) {
		SpringApplication.run(DaprSampleApplication.class, args);
	}

	@Bean
	public Supplier<Message<String>> supply() {
		return () -> {
			LOGGER.info("Sending message, sequence " + i);
			return MessageBuilder.withPayload("Hello world, " + i++).build();
		};
	}

	@Bean
	public Consumer<Message<String>> consume() {
		return message -> LOGGER.info("Message received, {}", message.getPayload());
	}
}
