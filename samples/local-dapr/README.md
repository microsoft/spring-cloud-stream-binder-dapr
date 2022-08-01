# LOCAL DAPR SAMPLE

This is a local dapr sample, demonstrates how to use `Spring Cloud Stream Dapr Binder` to send messages to `Azure Event Hubs` and receive messages from `Azure Event Hubs`.

## Prerequisites
- [Dapr CLI](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
- [Azure Event Hubs](https://docs.microsoft.com/azure/event-hubs/event-hubs-create)
- [Azure Storage Account](https://docs.microsoft.com/azure/storage/common/storage-account-create?tabs=azure-portal)

## QuickStart

### 1. Configure the Pub/Sub components to connect to Azure Event Hubs

`Dapr` integrates with `Pub/Sub` message buses to provide applications with the ability to create event-driven, loosely coupled architectures where producers send events to consumers via topics.

`Dapr` supports the configuration of multiple, named, `Pub/Sub components` per application. Each `Pub/Sub component` has a name and this name is used when publishing a message topic. 

`Pub/Sub components` are extensible. A list of support `Pub/Sub components` is [here](https://docs.dapr.io/reference/components-reference/supported-pubsub/) and the implementations can be found in the [components-contrib repo](https://github.com/dapr/components-contrib).

In this example, we configure the `Azure Event Hubs Pub/Sub component` described using the [pubsub.yaml](./components/pubsub.yaml) file:
```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  # Specifies the name of the Pub/Sub component
  name: eventhubs-pubsub
spec:
  type: pubsub.azure.eventhubs
  version: v1
  metadata:
    # Connection-string for the Event Hub or the Event Hub namespace
    - name: connectionString
      value: "<AZURE_CONNECTION_STRING>"
    # Storage account name to use for the EventProcessorHost
    - name: storageAccountName
      value: "<AZURE_STORAGE_ACCOUNT_NAME>"
    # Storage account key to use for the EventProcessorHost
    - name: storageAccountKey
      value: "<AZURE_STORAGE_ACCOUNT_KEY>"
    # Storage container name for the storage account name
    - name: storageContainerName
      value: "<AZURE_STORAGE_CONTAINER_NAME>"
```

Follow the instructions [here](https://docs.microsoft.com/azure/storage/common/storage-account-keys-manage?tabs=azure-portal) to manage the storage account access keys.
See [here](https://docs.microsoft.com/azure/event-hubs/event-hubs-get-connection-string) on how to get the Event Hubs connection string.


```yaml
spring:
  cloud:
    stream:
      function:
        definition: supply;consume
      bindings:
        supply-out-0:
          # Specifies the name of the entity to which the message is sent
          destination: <AZURE_EVENTHUB_NAME>
        consume-in-0:
          # Specifies the name of the entity to which the message is received
          destination: <AZURE_EVENTHUB_NAME>
      dapr:
        bindings:
          supply-out-0:
            producer:
              # Specifies the name of the Pub/Sub component to send the message
              pubsubName: eventhubs-pubsub
          consume-in-0:
            consumer:
              # Specifies the name of the Pub/Sub component to receive the message
              pubsubName: eventhubs-pubsub
```
Replace the `<AZURE_EVENTHUB_NAME>` in the [application.yaml](./src/main/resources/application.yaml).

### 2. Run Application with Dapr sidecar
```shell
cd local-dapr
dapr run --app-id local-dapr --app-port 9090 --components-path ./components  --app-protocol grpc --dapr-grpc-port 50001 mvn spring-boot:run
```

This command specifies:
- the id for your application with `--app-id local-dapr`, used for service discovery.
- the port your application is listening on (default -1) with `--app-port 9090`.
- the path for components directory with `--components-path ./components`.
- the protocol (gRPC or HTTP) Dapr with `--app-protocol grpc` uses to talk to the application.
- the gRPC port for Dapr to listen on (default -1) with `--dapr-grpc-port 50001`

> ***NOTE:***
> For every Dapr app that wants to subscribe to events, create an Event Hubs consumer group with the name of the `app-id`.



Then verify in your app’s logs that similar messages were posted:
```shell
Sending message, sequence 0
Success to publish event
Message received, Hello world, 0
Sending message, sequence 1
Success to publish event
Message received, Hello world, 1
...
```

### 3. Clean Up
To stop your services from running, simply stop the `dapr run` process. Alternatively, you can spin down your services with the Dapr CLI `dapr stop` command. 
```shell
dapr stop --app-id local-dapr
```
