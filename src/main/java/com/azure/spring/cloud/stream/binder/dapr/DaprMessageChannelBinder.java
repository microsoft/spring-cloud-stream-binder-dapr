// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr;

import com.azure.spring.cloud.stream.binder.dapr.impl.DaprMessageHandler;
import com.azure.spring.cloud.stream.binder.dapr.impl.DaprMessageProducer;
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
import org.springframework.util.Assert;

/**
 * {@link DaprMessageChannelBinder} extend the AbstractMessageChannelBinder class.
 * Providing the required constructors and overriding the inherited abstract methods.
 */
public class DaprMessageChannelBinder extends
		AbstractMessageChannelBinder<ExtendedConsumerProperties<DaprConsumerProperties>, ExtendedProducerProperties<DaprProducerProperties>, DaprBinderProvisioner>
		implements
		ExtendedPropertiesBinder<MessageChannel, DaprConsumerProperties, DaprProducerProperties> {

	private DaprGrpc.DaprStub daprStub;

	private DaprExtendedBindingProperties bindingProperties = new DaprExtendedBindingProperties();

	/**
	 * Construct a {@link DaprMessageChannelBinder} with the specified headers to embed and {@link DaprBinderProvisioner}.
	 *
	 * @param headersToEmbed the headers to embed
	 * @param provisioningProvider the provisioning provider
	 */
	public DaprMessageChannelBinder(String[] headersToEmbed,
			DaprBinderProvisioner provisioningProvider) {
		super(headersToEmbed, provisioningProvider);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<DaprProducerProperties> producerProperties, MessageChannel errorChannel) {
		Assert.notNull(getDaprStub(producerProperties.getExtension().getSidecarIp(), producerProperties.getExtension().getGrpcPort()), "daprStub can't be null when create a producer");
		DaprMessageHandler daprMessageHandler = new DaprMessageHandler(destination.getName(),
				producerProperties.getExtension().getPubsubName(), this.daprStub);
		daprMessageHandler.setMetadata(producerProperties.getExtension().getMetadata());
		daprMessageHandler.setSync(producerProperties.getExtension().isSync());
		daprMessageHandler.setSendTimeout(producerProperties.getExtension().getSendTimeout().toMillis());
		daprMessageHandler.setBeanFactory(getBeanFactory());
		return daprMessageHandler;
	}

	private DaprGrpc.DaprStub getDaprStub(String sidecarIp, Integer grpcPort) {
		if (this.daprStub == null) {
			ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(sidecarIp, grpcPort).usePlaintext().build();
			this.daprStub = DaprGrpc.newStub(managedChannel);
		}
		return this.daprStub;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ExtendedConsumerProperties<DaprConsumerProperties> consumerProperties) {
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

	/**
	 * Get bindingProperties.
	 *
	 * @return bindingProperties the bindingProperties
	 */
	public DaprExtendedBindingProperties getBindingProperties() {
		return bindingProperties;
	}

	/**
	 * Set bindingProperties.
	 *
	 * @param bindingProperties the bindingProperties
	 */
	public void setBindingProperties(DaprExtendedBindingProperties bindingProperties) {
		this.bindingProperties = bindingProperties;
	}
}
