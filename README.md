# spring-cloud-stream-binder-dapr

## Overview

This project implements a Binder called "Spring Cloud Stream Binder for Dapr" based on the Spring Cloud Stream framework. With this native integration, Spring Cloud Stream applications can use the coding style of Spring Cloud Stream's existing framework to send and receive messages.


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

## Samples

Please refer to [here](https://github.com/MouMangTai/spring-cloud-stream-binder-dapr-sample)
to run a sample.
