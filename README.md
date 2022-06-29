# spring-cloud-stream-binder-dapr

## Overview
The Spring Cloud Stream Binder for Dapr provides the binding implementation for the Spring Cloud Stream. 
This project implements a Binder called "Spring Cloud Stream Binder for Dapr" based on the Spring Cloud Stream framework. With this native integration, Spring Cloud Stream applications can use the coding style of Spring Cloud Stream's existing framework to send and receive messages.

The following simplified diagram shows how the Dapr binder operates:

<img width="665" alt="image" src="https://user-images.githubusercontent.com/42743274/176439470-64c42ea4-ebff-48a5-81a3-e3f11bb87387.png">
Figure 1. Dapr Binder

The Dapr Binder implementation maps each destination to a Topic. Our service will publish messages to specific topics, and then get messages by subscribing to topics.The Dapr Publish & Subscribe building block provides out-of-the-box messaging abstractions and implementations.Specify pubsubName to call the predefined Dapr pub/sub component.Dapr guarantees `at-least-once` semantics for message delivery. After a message is published, it is sent at least once to any interested subscribers.

## Usage

1. Add or update dependency

```
<dependency>
   <groupId>com.azure.spring</groupId>
   <artifactId>spring-cloud-azure-stream-binder-dapr</artifactId>
</dependency>
```

2. Update configuration

If the project has previously used message oriented middleware, you only need to remove the middleware configuration and keep the Spring Cloud Stream configuration.

If the project has not used message oriented middleware before, you only need to add the configuration related to messaging in Spring Cloud Stream.
```
spring:
  cloud:
    stream:
      function:
        definition: consume;supply
      bindings:
        consume-in-0:
          destination: ${TOPIC_NAME}
        supply-out-0:
          destination: ${TOPIC_NAME} # same as the above destination
      dapr:
        bindings:
          supply-out-0:
            producer:
              pubsubName: ${PUBSUB_NAME}
              sidecarIp: 127.0.0.1
              grpcPort: 50001
      poller:
        initial-delay: 0
        fixed-delay: 1000
```

3. Code

Directly use the coding method of the existing framework of Spring Cloud Stream, and define a consumer bean and a supplier bean.

```
@Bean
public Supplier<Message<String>> supply() {
    return () -> {
        LOGGER.info("Sending message, sequence " + i);
        return MessageBuilder.withPayload("Hello world, " + i++).build();
    };
}


@Bean
public Consumer<Message<String>> consume() {
    return message -> {
        LOGGER.info("New message received: '{}'", message.getPayload());
    };
}
```
## Configuration Options
This section contains settings specific to the Dapr Binder and bound channels.

For general binding configuration options and properties, please refer to the Spring Cloud Stream core documentation.

### Dapr Producer Properties

The following properties are available for Dapr producers only and must be prefixed with `spring.cloud.stream.dapr.bindings.<bindingTarget>.producer.`.

#### sync
Whether messages should be written to the stream in a synchronous manner. If true, the producer will wait for a reply from Dapr after a `publishEvent` operation.

Default: `false`

#### sendTimeout
Only valid if sync is set to true, the time in milliseconds to wait for a response from Dapr after a `publishEvent` operation.

Default: `10000`

#### pubsubName
Specifies the name of the Pub/Sub component.

Default: `pubsub`
#### sidecarIp
The parameter of the channel, indicating the IP address of the dapr sidecar to which the message is finally sent through the channel.

Default: `127.0.0.1`
#### grpcPort
The parameter of channel, indicating the grpc port that the dapr sidecar to which the message is finally sent through the channel listens.

Default: `50001`
#### contentType
The contentType tells Dapr which content type your data adheres to when constructing a CloudEvent envelope.

Default: `application/json`
#### metadata
The following properties are available for metadata only and must be prefixed with `spring.cloud.stream.dapr.bindings.<bindingTarget>.producer.metadata.`.

Additional metadata parameters are available based on each pubsub component.
##### ttlInSeconds
the number of seconds for the message to expire.If not set, it will not expire.
##### rawPayload
boolean to determine if Dapr should publish the event without wrapping it as CloudEvent.

Default: `false`

## Samples

Please refer to [here](https://github.com/MouMangTai/spring-cloud-stream-binder-dapr-sample)
to run a sample.
