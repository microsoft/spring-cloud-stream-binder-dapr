// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr;

import com.azure.spring.cloud.stream.binder.dapr.properties.DaprConsumerProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprProducerProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import io.dapr.v1.DaprGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
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
		AbstractMessageChannelBinder<ExtendedConsumerProperties<DaprConsumerProperties>, ExtendedProducerProperties<DaprProducerProperties>, DaprBinderProvisioner>
		implements
		ExtendedPropertiesBinder<MessageChannel, DaprConsumerProperties, DaprProducerProperties> {
	private DaprExtendedBindingProperties bindingProperties = new DaprExtendedBindingProperties();
	/**
	 * Construct a {@link DaprMessageChannelBinder} with the specified headers to embed and {@link DaprBinderProvisioner}.
	 *
	 * @param headersToEmbed the headers to enbed
	 * @param provisioningProvider the provisioning provider
	 */
	public DaprMessageChannelBinder(String[] headersToEmbed,
			DaprBinderProvisioner provisioningProvider) {
		super(headersToEmbed, provisioningProvider);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<DaprProducerProperties> producerProperties, MessageChannel errorChannel) throws Exception {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(
				producerProperties.getExtension().getSidecarIp(),
				producerProperties.getExtension().getGrpcPort()).usePlaintext().build();
		DaprGrpc.DaprFutureStub futureStub = DaprGrpc.newFutureStub(managedChannel);
		DaprMessageHandler daprMessageHandler = new DaprMessageHandler(futureStub, destination.getName());
		daprMessageHandler.setPubsubName(producerProperties.getExtension().getPubsubName());
		return daprMessageHandler;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ExtendedConsumerProperties<DaprConsumerProperties> consumerProperties) throws Exception {
		return new DaprMessageProducer();
	}

	@Override
	public DaprConsumerProperties getExtendedConsumerProperties(String destination) {
		return this.bindingProperties.getExtendedConsumerProperties(destination);
	}

	@Override
	public DaprProducerProperties getExtendedProducerProperties(String destination) {
		return this.bindingProperties.getExtendedProducerProperties(destination);
	}

	@Override
	public String getDefaultsPrefix() {
		return this.bindingProperties.getDefaultsPrefix();
	}

	@Override
	public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
		return this.bindingProperties.getExtendedPropertiesEntryClass();
	}

	public DaprExtendedBindingProperties getBindingProperties() {
		return bindingProperties;
	}

	public void setBindingProperties(DaprExtendedBindingProperties bindingProperties) {
		this.bindingProperties = bindingProperties;
	}
}
