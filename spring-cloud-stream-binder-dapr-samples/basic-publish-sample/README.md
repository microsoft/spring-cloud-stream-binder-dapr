# Spring cloud stream binder for dapr samples

This is a publish sample of Spring Cloud Stream Binder Dapr, demonstrate how to use dapr binder to send messages. 
`Publiser` generates messages and publishes to a specific topic, and `subscriber` subscribers listen for messages of topic orders.

This sample includes one publisher:

- Java client message generator `publisher`

And one subscriber:

- Java subscriber `subscriber`

## Pre-requisites
- [Dapr and Dapr Cli](https://docs.dapr.io/getting-started/install-dapr-cli/).

## QuickStart

### 1. Run Message Subscriber app with Dapr
> **Note:**
> You also could refer to [Dapr quickstart](https://github.com/dapr/quickstarts/tree/master/pub_sub/java/sdk#run-java-message-subscriber-app-with-dapr) to run Java message subscriber app with Dapr.
```shell
cd subscriber
dapr run --app-id subscriber --app-port 9090 --components-path ../../components  --app-protocol grpc --dapr-grpc-port 50000 mvn spring-boot:run
```
You can simply just run dapr, but you won't see the printout.
```shell
dapr run --app-id subscriber --app-port 9090 --components-path ../../components  --app-protocol grpc --dapr-grpc-port 50000
```
### 2. Run Message Publisher app which depend on Dapr binder
```shell
cd publisher
dapr run --app-id publisher --app-port 9091 --components-path ../../components  --app-protocol grpc --dapr-grpc-port 50001 mvn spring-boot:run
```

### 3. Output

```shell
# publisher
Sending message, sequence 0
Success to publish event
Sending message, sequence 1
Success to publish event
Sending message, sequence 2
Success to publish event
Sending message, sequence 3
Success to publish event
Sending message, sequence 4
Success to publish event
...

# subscriber
------onTopicEvent------
TopicEventRequest :
id: "34e3efdb-20bc-4551-83bf-432049e6b1f9"
source: "publisher"
type: "com.dapr.event.sent"
spec_version: "1.0"
data_content_type: "application/json"
topic: "topic"
data: "\"SGVsbG8gd29ybGQsIDEx\""
pubsub_name: "redis-pubsub"
...
```

### 4. Stop Dapr Sidecar
```shell
dapr stop --app-id publisher
dapr stop --app-id subscriber
```
