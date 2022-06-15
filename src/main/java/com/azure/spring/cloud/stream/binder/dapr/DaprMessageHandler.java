package com.azure.spring.cloud.stream.binder.dapr;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class DaprMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String TOPIC_NAME = "orders";
        String PUBSUB_NAME = "orderpubsub";

        DaprClient client = new DaprClientBuilder().build();

        // Publish an event/message using Dapr PubSub
        client.publishEvent(
                PUBSUB_NAME,
                TOPIC_NAME,
                message).block();
    }
}
