# Spring cloud stream binder for dapr samples

This is a publish sample of Spring Cloud Stream Binder Dapr, demonstrate how to use dapr binder to send messages. 

## Pre-requisites
- [Dapr and Dapr Cli](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Azure Event Hub](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-create)
- [Azure Storage Account](https://docs.microsoft.com/zh-cn/azure/storage/common/storage-account-create?tabs=azure-portal)
## QuickStart

### 1. Update eventhubs-pubsub.yaml

Modify the following configuration in the `eventhubs-pubsub` yaml file according to the created resource.
- connectionString
- storageAccountName
- storageAccountKey
- storageContainerName

For other configurable parameters, please refer to [setup-azure-eventhubs](https://docs.dapr.io/zh-hans/reference/components-reference/supported-pubsub/setup-azure-eventhubs/).
### 2. Run Application with Dapr sidecar
```shell
cd spring-cloud-stream-binder-dapr-sample
dapr run --app-id sample --app-port 9090 --components-path ./components  --app-protocol grpc --dapr-grpc-port 50001 mvn spring-boot:run
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
source: "sample"
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
dapr stop --app-id sample
```
