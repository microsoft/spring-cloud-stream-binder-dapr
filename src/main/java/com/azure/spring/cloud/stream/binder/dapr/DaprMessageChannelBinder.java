package com.azure.spring.cloud.stream.binder.dapr;

import com.azure.spring.cloud.stream.binder.dapr.provisioner.DaprMessageBinderProvisioner;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

public class DaprMessageChannelBinder
        extends AbstractMessageChannelBinder<ConsumerProperties, ProducerProperties, DaprMessageBinderProvisioner> {

    public DaprMessageChannelBinder(String[] headersToEmbed, DaprMessageBinderProvisioner provisioningProvider) {
        super(headersToEmbed, provisioningProvider);
    }

    @Override
    protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ProducerProperties producerProperties, MessageChannel errorChannel) throws Exception {
        return new DaprMessageHandler();
    }

    @Override
    protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group, ConsumerProperties properties) throws Exception {
        return new DaprMessageProducer();
    }
}
