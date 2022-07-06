// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr;

import com.azure.spring.cloud.stream.binder.dapr.impl.DaprMessageHandler;
import com.azure.spring.cloud.stream.binder.dapr.impl.DaprMessageProducer;
import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprConsumerProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprProducerProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import io.dapr.v1.DaprGrpc;

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
		AbstractMessageChannelBinder<
				ExtendedConsumerProperties<DaprConsumerProperties>,
				ExtendedProducerProperties<DaprProducerProperties>,
				DaprBinderProvisioner>
		implements
		ExtendedPropertiesBinder<MessageChannel, DaprConsumerProperties, DaprProducerProperties> {

	private final DaprGrpc.DaprStub daprStub;
	private final DaprExtendedBindingProperties bindingProperties;
	private final DaprMessageConverter daprMessageConverter;

	public DaprMessageChannelBinder(String[] headersToEmbed,
			DaprBinderProvisioner provisioningProvider,
			DaprGrpc.DaprStub daprStub,
			DaprExtendedBindingProperties bindingProperties,
			DaprMessageConverter daprMessageConverter) {
		super(headersToEmbed, provisioningProvider);
		this.daprStub = daprStub;
		this.bindingProperties = bindingProperties;
		this.daprMessageConverter = daprMessageConverter;
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<DaprProducerProperties> producerProperties,
			MessageChannel errorChannel) {
		DaprProducerProperties extension = producerProperties.getExtension();
		DaprMessageHandler daprMessageHandler =
				new DaprMessageHandler(destination.getName(), extension.getPubsubName(), daprStub, daprMessageConverter);
		daprMessageHandler.setBeanFactory(getBeanFactory());
		return daprMessageHandler;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ExtendedConsumerProperties<DaprConsumerProperties> consumerProperties) {
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
}
