# Spring Cloud Stream Binder Dapr

This guide describes the Dapr implementation of the Spring Cloud Stream Binder.
It contains information about its design, usage and configuration options, as well as information on how the Stream Cloud Stream concepts map into Dapr specific constructs.

### 1. Usage

To use Dapr binder, you need to add `spring-cloud-stream-binder-dapr` as a dependency to your Spring Cloud Stream application, as shown in the following example for Maven:
```xml
<dependency>
  <groupId>com.azure.spring</groupId>
  <artifactId>spring-cloud-stream-binder-dapr</artifactId>
</dependency>
```

Alternatively, you can use the Spring Cloud Stream Dapr Starter, as follows:

```xml
<dependency>
  <groupId>com.azure.spring</groupId>
  <artifactId>spring-cloud-starter-stream-dapr</artifactId>
</dependency>
```

## 2. Dapr Binder Overview

Dapr uses a modular design where functionality is delivered as a component. Each component has an interface definition, All of the components are pluggable.
The Pub Sub components provide a common way to interact with different message bus implementations to achieve reliable, high-scale scenarios based on event-driven async communications, while allowing users to opt-in to advanced capabilities using defined metadata.

Spring Cloud Stream Binder abstraction makes it possible for a Spring Cloud Stream application to be flexible in how it connects to middleware.

Combining spring cloud stream binder and dapr, on the one hand dapr can make up the dependent on a specific middleware library for Spring Cloud Stream, on the other hand, spring cloud stream can help dapr and dapr client decoupling.Therefore, the combination of the two is conducive to the maximum decoupling and enhanced features of both sides.

The following simplified diagram shows how the Dapr binder operates:

<img width="665" alt="image" src="https://user-images.githubusercontent.com/42743274/176439470-64c42ea4-ebff-48a5-81a3-e3f11bb87387.png">
Figure 1. Dapr Binder

The Dapr Binder implementation maps each destination to a Dapr `Topic`.
The message is sent to the specified topic, and then get message by subscribing to topic. For each consumer group, a Queue is bound to that `Topic`.
Each consumer instance has a corresponding Dapr Consumer instance for its group’s Queue.
Specify pubsubName to call the predefined Dapr Pub Sub component.

## 3. Configuration Options

This section contains the configuration options used by the Dapr binder.

For common configuration options and properties pertaining to the binder, see the [binding properties](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/#_configuration_options) in core documentation.

### 3.1. Dapr Binder Properties

The following properties are available for Dapr binder only and must be prefixed with `spring.cloud.stream.dapr.binder.`.

#### spring.cloud.stream.dapr.binder.daprIp

The parameter of the channel, indicating the IP address of the dapr sidecar to which the message is finally sent through the channel.

Default: `127.0.0.1`

#### spring.cloud.stream.dapr.binder.daprPort

The parameter of channel, indicating the port that the dapr sidecar to which the message is finally sent through the channel listens.

Default: `50001`

#### spring.cloud.stream.dapr.binder.managedChannel.negotiationType

NegotiationType decide which connection method to use. TLS and PLAINTEXT are currently available.

- PLAINTEXT: Use of a plaintext connection to the server. By default a secure connection mechanism such as TLS will be used.
  Should only be used for testing or for APIs where the use of such API or the data exchanged is not sensitive.
  This assumes prior knowledge that the target of this channel is using plaintext. It will not perform HTTP/1.1 upgrades.
- TLS: Makes the client use TLS.

Default: `PLAINTEXT`

#### spring.cloud.stream.dapr.binder.managedChannel.authority

Overrides the authority used with TLS and HTTP virtual hosting. It does not change what host is actually connected to. Is commonly in the form host:port.

This method is intended for testing, but may safely be used outside of tests as an alternative to DNS overrides.

#### spring.cloud.stream.dapr.binder.managedChannel.defaultLoadBalancingPolicy

Sets the default load-balancing policy that will be used if the service config doesn't specify one.

This method is implemented by all stock channel builders that are shipped with gRPC, but may not be implemented by custom channel builders, in which case this method will throw.

#### spring.cloud.stream.dapr.binder.managedChannel.idleTimeout

Set the duration without ongoing RPCs before going to idle mode.

In idle mode the channel shuts down all connections, the NameResolver and the LoadBalancer. A new RPC would take the channel out of idle mode. A channel starts in idle mode. Defaults to 30 minutes.

This is an advisory option. Do not rely on any specific behavior related to this option.

TimeUnit: `minutes`

#### spring.cloud.stream.dapr.binder.managedChannel.keepAliveTime

Sets the time without read activity before sending a keepalive ping. An unreasonably small value might be increased, and Long.MAX_VALUE nano seconds or an unreasonably large value will disable keepalive. Defaults to infinite.

Clients must receive permission from the service owner before enabling this option. Keepalives can increase the load on services and are commonly "invisible" making it hard to notice when they are causing excessive load. Clients are strongly encouraged to use only as small of a value as necessary.

TimeUnit: `minutes`

#### spring.cloud.stream.dapr.binder.managedChannel.keepAliveTimeout

Sets the time waiting for read activity after sending a keepalive ping. If the time expires without any read activity on the connection, the connection is considered dead. An unreasonably small value might be increased. Defaults to 20 seconds.

This value should be at least multiple times the RTT to allow for lost packets.

TimeUnit: `seconds`

#### spring.cloud.stream.dapr.binder.managedChannel.perRpcBufferLimit

Sets the per RPC buffer limit in bytes used for retry. The RPC is not retriable if its buffer limit is exceeded. The implementation may only estimate the buffer size being used rather than count the exact physical memory allocated. It does not have any effect if retry is disabled by the client.

This method may not work as expected for the current release because retry is not fully implemented yet.

#### spring.cloud.stream.dapr.binder.managedChannel.retryBufferSize

Sets the retry buffer size in bytes. If the buffer limit is exceeded, no RPC could retry at the moment, and in hedging case all hedges but one of the same RPC will cancel. The implementation may only estimate the buffer size being used rather than count the exact physical memory allocated. The method does not have any effect if retry is disabled by the client.

This method may not work as expected for the current release because retry is not fully implemented yet.

#### spring.cloud.stream.dapr.binder.managedChannel.keepAliveWithoutCalls

Sets whether keepalive will be performed when there are no outstanding RPC on a connection. Defaults to false.

Clients must receive permission from the service owner before enabling this option. Keepalives on unused connections can easilly accidentally consume a considerable amount of bandwidth and CPU. `idleTimeout()` should generally be used instead of this option.

#### spring.cloud.stream.dapr.binder.managedChannel.maxInboundMessageSize

Sets the maximum message size allowed to be received on the channel. If not called, defaults to 4 MiB. The default provides protection to clients who haven't considered the possibility of receiving large messages while trying to be large enough to not be hit in normal usage.

This method is advisory, and implementations may decide to not enforce this. Currently, the only known transport to not enforce this is InProcessTransport.

#### spring.cloud.stream.dapr.binder.managedChannel.maxInboundMetadataSize

Sets the maximum size of metadata allowed to be received. Integer.MAX_VALUE disables the enforcement. The default is implementation-dependent, but is not generally less than 8 KiB and may be unlimited.

This is cumulative size of the metadata. The precise calculation is implementation-dependent, but implementations are encouraged to follow the calculation used for [HTTP/2's SETTINGS_MAX_HEADER_LIST_SIZE](https://httpwg.org/specs/rfc7540.html#rfc.section.6.5.2) . It sums the bytes from each entry's key and value, plus 32 bytes of overhead per entry.

#### spring.cloud.stream.dapr.binder.managedChannel.maxRetryAttempts

Sets the maximum number of retry attempts that may be configured by the service config. If the service config specifies a larger value it will be reduced to this value. Setting this number to zero is not effectively the same as disableRetry() because the former does not disable [transparent retry](https://github.com/grpc/proposal/blob/master/A6-client-retries.md#transparent-retries) .

This method may not work as expected for the current release because retry is not fully implemented yet.

#### spring.cloud.stream.dapr.binder.managedChannel.maxHedgedAttempts

Sets the maximum number of hedged attempts that may be configured by the service config. If the service config specifies a larger value it will be reduced to this value.

This method may not work as expected for the current release because retry is not fully implemented yet.

#### spring.cloud.stream.dapr.binder.managedChannel.maxTraceEvents

Sets the maximum number of channel trace events to keep in the tracer for each channel or subchannel. If set to 0, channel tracing is effectively disabled.

#### spring.cloud.stream.dapr.binder.daprStub.maxInboundMessageSize

Limits the maximum acceptable message size from a remote peer.

#### spring.cloud.stream.dapr.binder.daprStub.maxOutboundMessageSize

Limits the maximum acceptable message size to send a remote peer.

#### spring.cloud.stream.dapr.binder.daprStub.compression

Set's the compressor name to use for the call. It is the responsibility of the application to make sure the server supports decoding the compressor picked by the client. To be clear, this is the compressor used by the stub to compress messages to the server.

### 3.2. Dapr Producer Properties

The following properties are available for Dapr producers only and must be prefixed with `spring.cloud.stream.dapr.bindings.<bindingTarget>.producer.`.

#### pubsubName
Specifies the name of the Pub/Sub component.

### 3.3 Dapr Consumer Properties

The following properties are available for Dapr producers only and must be prefixed with `spring.cloud.stream.dapr.bindings.<bindingTarget>.consumer.`.

#### pubsubName
Specifies the name of the Pub/Sub component.

### 3.4 Dapr Message Headers

The following table illustrates how Dapr message properties are mapped to Spring message headers.

| Dapr<br/>Message<br/>Properties | Spring Message Header Constants       | Type                 | Description                                                                                                                                                                                                                                           |
|:--------------------------------|---------------------------------------|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contentType                     | DaprHeaders#CONTENT_TYPE              | String               | The contentType tells Dapr which content type your data adheres to when constructing a CloudEvent envelope.                                                                                                                                           |
| ttlInSeconds                    | DaprHeaders#TTL_IN_SECONDS            | Long                 | The number of seconds for the message to expire.                                                                                                                                                                                                      |
| rawPayload                      | DaprHeaders#RAW_PAY_LOAD              | Boolean              | Determine if Dapr should publish the event without wrapping it as CloudEvent. Not using CloudEvents disables support for tracing, event deduplication per messageId, content-type metadata, and any other features built using the CloudEvent schema. |
| specifiedBrokerMetadata         | DaprHeaders#SPECIFIED_Broker_METADATA | Map<String, String>  | Some metadata parameters are available based on each pubsub broker component.                                                                                                                                                                         |

# Appendices
## Building

### Basic Compile and Test

Pre-requisites:

- To compile, JDK 1.8 installed.

The build uses the Maven wrapper so you don’t have to install a specific version of Maven. The main build command is

```shell
$ ./mvnw clean install
```

You can also add `-DskipTests` if you like, to avoid running the tests.

> ***Note:***
> You can also install Maven (>=3.3.3) yourself and run the `mvn` command in place of `./mvnw` in the examples below.

> ***Note:***
> Be aware that you might need to increase the amount of memory available to Maven by setting a `MAVEN_OPTS` environment variable with a value like `-Xmx512m -XX:MaxPermSize=128m`.
We try to cover this in the `.mvn` configuration, so if you find you have to do it to make a build succeed, please raise a ticket to get the settings added to source control.


### Working with the code
If you don’t have an IDE preference we would recommend that you use [Spring Tools Suite](https://www.springsource.com/developer/sts) or [Eclipse](https://www.eclipse.org) when working with the code. We use the [m2eclipe](https://www.eclipse.org/m2e/) eclipse plugin for maven support. Other IDEs and tools should also work without issue.

#### Importing into eclipse with m2eclipse

We recommend the [m2eclipe](https://www.eclipse.org/m2e/) eclipse plugin when working with eclipse. If you don’t already have m2eclipse installed it is available from the "eclipse marketplace".

Unfortunately m2e does not yet support Maven 3.3, so once the projects are imported into Eclipse you will also need to tell m2eclipse to use the `.settings.xml` file for the projects. If you do not do this you may see many different errors related to the POMs in the projects. Open your Eclipse preferences, expand the Maven preferences, and select User Settings. In the User Settings field click Browse and navigate to the Spring Cloud project you imported selecting the `.settings.xml` file in that project. Click Apply and then OK to save the preference changes.

> ***Note:***
> Alternatively you can copy the repository settings from [.settings.xml](https://github.com/spring-cloud/spring-cloud-build/blob/main/.settings.xml) into your own `~/.m2/settings.xml`.

#### Importing into eclipse without m2eclipse

If you prefer not to use m2eclipse you can generate eclipse project metadata using the following command:

```shell
$ ./mvnw eclipse:eclipse
```
The generated eclipse projects can be imported by selecting `import existing projects` from the `file` menu.

## Contributing
Spring Cloud is released under the non-restrictive Apache 2.0 license, and follows a very standard Github development process, using Github tracker for issues and merging pull requests into master. If you want to contribute even something trivial please do not hesitate, but follow the guidelines below.

### Sign the Contributor License Agreement
Before we accept a non-trivial patch or pull request we will need you to sign the contributor’s agreement. Signing the contributor’s agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.

### Code Conventions and Housekeeping
None of these is essential for a pull request, but they will all help. They can also be added after the original pull request but before a merge.

- Use the Spring Framework code format conventions. If you use Eclipse you can import formatter settings using the `eclipse-code-formatter.xml` file from the [Spring Cloud Build](https://github.com/spring-cloud/build/tree/master/eclipse-coding-conventions.xml) project. If using IntelliJ, you can use the [Eclipse Code Formatter Plugin](https://plugins.jetbrains.com/plugin/6546-adapter-for-eclipse-code-formatter/) to import the same file.
- Make sure all new `.java` files to have a simple Javadoc class comment with at least an `@author` tag identifying you, and preferably at least a paragraph on what the class is for.
- Add the MIT license header comment to all new `.java` files (copy from existing files in the project).
- Add yourself as an `@author` to the .java files that you modify substantially (more than cosmetic changes).
- Add some Javadocs and, if you change the namespace, some XSD doc elements.
- A few unit tests would help a lot as well — someone has to do it.
- If no-one else is using your branch, please rebase it against the current master (or other target branch in the main project).
- When writing a commit message please follow [these conventions](https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html), if you are fixing an existing issue please add Fixes gh-XXXX at the end of the commit message (where XXXX is the issue number).
