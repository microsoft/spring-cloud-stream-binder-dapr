// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr;

import com.azure.spring.cloud.stream.binder.dapr.provisioner.DaprMessageBinderProvisioner;
import io.dapr.v1.DaprGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * {@link DaprMessageChannelBinder} extend the AbstractMessageChannelBinder class.
 * Providing the required constructors and overriding the inherited abstract methods.
 */
public class DaprMessageChannelBinder extends
		AbstractMessageChannelBinder<ConsumerProperties,
		ProducerProperties,
		DaprMessageBinderProvisioner> {

	/**
	 * Construct a {@link DaprMessageChannelBinder} with the specified headers to embed and {@link DaprMessageBinderProvisioner}.
	 *
	 * @param headersToEmbed the headers to enbed
	 * @param provisioningProvider the provisioning provider
	 */
	public DaprMessageChannelBinder(String[] headersToEmbed,
			DaprMessageBinderProvisioner provisioningProvider) {
		super(headersToEmbed, provisioningProvider);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ProducerProperties producerProperties,
			MessageChannel errorChannel) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress(
				"127.0.0.1", 50001).usePlaintext().build();
		DaprGrpc.DaprStub daprStub = DaprGrpc.newStub(channel);
		return new DaprMessageHandler(daprStub);
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ConsumerProperties properties) throws Exception {
		return new DaprMessageProducer();
	}
}
