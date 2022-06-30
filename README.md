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

Default: `no limit`
##### rawPayload
boolean to determine if Dapr should publish the event without wrapping it as CloudEvent.

Default: `false`

## Building

### Basic Compile and Test

Pre-requisites:

- To compile, JDK 1.8 installed.

The build uses the Maven wrapper so you don’t have to install a specific version of Maven. The main build command is

```
$ ./mvnw clean install
```

You can also add `-DskipTests` if you like, to avoid running the tests.


> ***Note:***
> You can also install Maven (>=3.3.3) yourself and run the `mvn` command in place of `./mvnw` in the examples below. If you do that you also might need to add `-P spring` if your local Maven settings do not contain repository declarations for spring pre-release artifacts.

> ***Note:***
> Be aware that you might need to increase the amount of memory available to Maven by setting a `MAVEN_OPTS` environment variable with a value like `-Xmx512m -XX:MaxPermSize=128m`. We try to cover this in the `.mvn` configuration, so if you find you have to do it to make a build succeed, please raise a ticket to get the settings added to source control.

### Working with the code
If you don’t have an IDE preference we would recommend that you use [Spring Tools Suite](https://www.springsource.com/developer/sts) or [Eclipse](https://www.eclipse.org) when working with the code. We use the [m2eclipe](https://www.eclipse.org/m2e/) eclipse plugin for maven support. Other IDEs and tools should also work without issue.

#### Importing into eclipse with m2eclipse

We recommend the [m2eclipe](https://www.eclipse.org/m2e/) eclipse plugin when working with eclipse. If you don’t already have m2eclipse installed it is available from the "eclipse marketplace".

Unfortunately m2e does not yet support Maven 3.3, so once the projects are imported into Eclipse you will also need to tell m2eclipse to use the `.settings.xml` file for the projects. If you do not do this you may see many different errors related to the POMs in the projects. Open your Eclipse preferences, expand the Maven preferences, and select User Settings. In the User Settings field click Browse and navigate to the Spring Cloud project you imported selecting the `.settings.xml` file in that project. Click Apply and then OK to save the preference changes.

> ***Note:***
> Alternatively you can copy the repository settings from [.settings.xml](https://github.com/spring-cloud/spring-cloud-build/blob/main/.settings.xml) into your own `~/.m2/settings.xml`.

#### Importing into eclipse without m2eclipse

If you prefer not to use m2eclipse you can generate eclipse project metadata using the following command:

```
$ ./mvnw eclipse:eclipse
```
The generated eclipse projects can be imported by selecting `import existing projects` from the `file` menu.
## Samples

Please refer to [here](https://github.com/MouMangTai/spring-cloud-stream-binder-dapr-sample)
to run a sample.
