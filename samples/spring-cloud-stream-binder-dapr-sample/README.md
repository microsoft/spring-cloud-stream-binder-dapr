# Spring cloud stream binder for dapr samples

This is a publisher sample of usage of a Spring Cloud Stream binder for Dapr.

## Pre-requisites

- Message receiver App with Dapr with the following properties
  - The protocol Dapr uses to talk to the application: **Grpc**
  - The gRPC port for Dapr to listen on: **50001**
  - Dapr component name: **"orderpubsub"**
  - Topic name: **"orders"**
## QuickStart

### 1. Run Java message receiver app with Dapr

> **Note:** If you already have a message receiving application running through dapr and meet the requirements in Pre-requisites, you can skip this step.

Please refer to [here](https://github.com/dapr/quickstarts/tree/master/pub_sub/java/sdk#run-java-message-subscriber-app-with-dapr)
to run Java message receiver app with Dapr.

### 2. Run Sample
1. Install the *com.azure.spring.cloud.stream.binder.dapr* artifact in your local repository by moving to the main project folder and running: 
`mvn clean install`

2. Move back to the spring-cloud-stream-binder-dapr-sample directory and run the application with: `mvn spring-boot:run`

### 3. Output

```
Sending message, sequence 1
Sending message, sequence 2
Sending message, sequence 3
Sending message, sequence 4
Sending message, sequence 5
...
```
## How it work

