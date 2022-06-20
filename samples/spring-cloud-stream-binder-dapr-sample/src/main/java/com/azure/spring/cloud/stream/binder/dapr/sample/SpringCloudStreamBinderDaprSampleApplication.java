/*
 * Source code recreated from a .class file by IntelliJ IDEA
 * (powered by FernFlower decompiler)
 */

package com.azure.spring.cloud.stream.binder.dapr.sample;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class SpringCloudStreamBinderDaprSampleApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(com.azure.spring.cloud.stream.binder.dapr.sample.PublisherConfiguration.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamBinderDaprSampleApplication.class, args);
	}

	@Bean
	public Consumer<Message<String>> consume() {
		return message -> {
			LOGGER.info("New message received: '{}'", message.getPayload());
		};
	}
}
