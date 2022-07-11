# Spring cloud stream binder for dapr sample

This is a publish sample of Spring Cloud Stream Binder Dapr, demonstrate how to use dapr binder to send messages. 

## Pre-requisites
- [Dapr and Dapr Cli](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Azure Event Hub](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-create)
- [Azure Storage Account](https://docs.microsoft.com/zh-cn/azure/storage/common/storage-account-create?tabs=azure-portal)
## QuickStart

### 1. Update eventhubs-pubsub.yaml

Follow the instructions [here](https://docs.microsoft.com/zh-cn/azure/event-hubs/event-hubs-create) on setting up Azure Event Hubs. Since this implementation uses the Event Processor Host, you will also need an [Azure Storage Account](https://docs.microsoft.com/zh-cn/azure/storage/common/storage-account-create?tabs=azure-portal).

See [here](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-get-connection-string) on how to get the Event Hubs connection string. Note this is not the Event Hubs namespace.

> ***Note***
> To setup Azure Event Hubs pubsub create a component of type `pubsub.azure.eventhubs`. See [this guide](https://docs.dapr.io/developing-applications/building-blocks/pubsub/howto-publish-subscribe/#step-1-setup-the-pubsub-component) on how to create and apply a pubsub configuration.

Modify the following configuration in the `eventhubs-pubsub` yaml file according to the created resource.
- connectionString
- storageAccountName
- storageAccountKey
- storageContainerName

### 2. Run Application with Dapr sidecar
```shell
cd spring-cloud-stream-binder-dapr-sample
dapr run --app-id publisher --app-port 9090 --components-path ./components  --app-protocol grpc --dapr-grpc-port 50001 mvn spring-boot:run
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
data: data: "{\"payload\":\"SGVsbG8gd29ybGQsIDM=\",\"headers\":{\"contentType\":\"application/json\",\"id\":\"7feb48ab-f55d-4f74-7292-23a458080cf9\",\"timestamp\":1657504528684}}"
pubsub_name: "eventhubs-pubsub"
...
```

### 4. Stop Dapr Sidecar
```shell
dapr stop --app-id publisher
```
