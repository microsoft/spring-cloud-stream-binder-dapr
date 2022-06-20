/*
 * Source code recreated from a .class file by IntelliJ IDEA
 * (powered by FernFlower decompiler)
 */

package com.azure.spring.cloud.stream.binder.dapr.sample;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Configuration
@Profile("!manual")
public class PublisherConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(com.azure.spring.cloud.stream.binder.dapr.sample.PublisherConfiguration.class);
	private int i = 0;

	@Bean
	public Supplier<Message<String>> supply() {
		return () -> {
			LOGGER.info("Sending message, sequence " + i);
			return MessageBuilder.withPayload("Hello world, " + i++).build();
		};
	}
}
