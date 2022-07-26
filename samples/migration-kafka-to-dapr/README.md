# MIGRATION KAFKA TO DAPR

This example shows how to quickly migrate an application from using `Kafka Binder` to using `Dapr Binder` by switching different `maven profiles` and deploy it to `Azure Container Apps`.

The main application contains the reactive processor that receives the sensor data for a duration (3 seconds) and averages them. It then sends the average data (per sensor id) through the outbound destination of the processor.

The application also provides a source and sink for testing. Test source will generate some sensor data every 100 milliseconds and the test sink will verify that the processor has calculated the average. Test source is bound to the same broker destination where the processor is listening from. Similarly test sink is bound to the same broker destination where the processor is producing to.

## Prerequisites
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
- [Azure Event Hubs](https://docs.microsoft.com/azure/event-hubs/event-hubs-quickstart-cli)
- [Azure Resource Group](https://docs.microsoft.com/azure/azure-resource-manager/management/manage-resource-groups-cli)
- [Azure Container Apps Environment](https://docs.microsoft.com/azure/container-apps/get-started?tabs=bash#create-an-environment)
- [Docker](https://www.docker.com/get-started/)

## Run sample application using Kafka Binder
Event Hubs provides an endpoint compatible with the Apache Kafka® producer and consumer APIs that can be used by most existing Apache Kafka client applications as an alternative to running your own Apache Kafka cluster. Event Hubs supports Apache Kafka's producer and consumer APIs clients at version 1.0 and above.

Replace `${EVENTHUBS_NAMESPACE_NAME}` with your Event Hubs namespace name and the `${CONNECTION_STRING}` with the connection string for your Event Hubs namespace. For instructions on getting the connection string, see [Get an Event Hubs connection string](https://docs.microsoft.com/azure/event-hubs/event-hubs-get-connection-string). Here's an example configuration: `sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="Endpoint=sb://mynamespace.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=XXXXXXXXXXXXXXXX";`
```yaml
spring:
  cloud:
    stream:
      kafka:
        binder:
          # Required. Kafka broker connection setting
          brokers: ${EVENTHUBS_NAMESPACE_NAME}.servicebus.windows.net:9093
  kafka:
    security:
      protocol: SASL_SSL
    properties:
      sasl:
        mechanism: PLAIN
        jaas:
          config: org.apache.kafka.common.security.plain.PlainLoginModule required username="$ConnectionString" password="${CONNECTION_STRING}";
```

Update the `<properties>` collection in the [pom.xml](./pom.xml) file.
Your free [Docker Account](https://docs.docker.com/docker-id/) grants you access to `Docker Hub repository`.
```xml
<properties>
	<docker.account.username>username</docker.account.username>
	<docker.account.password>password</docker.account.password>
</properties>
```

Run the following command to build your application as a [Docker image](https://docs.docker.com/engine/reference/commandline/image/) and push it to your [Docker Hub repository](https://docs.docker.com/docker-hub/repos/).
Specify the profile by configuring `-Pkafka` to load `spring-cloud-stream-binder-kafka` dependency and `application-kafka.yml` configuration file.
```shell
mvn clean spring-boot:build-image -Pkafka
```

Run the following command to deploy the `Docker image` generated by the current application to `Azure Container Apps`.
```shell
az containerapp create \
  --name <CONTAINER_APP_NAME> \
  --resource-group <YOUR_RESOURCE_GROUP_NAME> \
  --environment <YOUR_CONTAINER_APP_ENVIRONMENT_NAME> \
  --image docker.io/<docker.account.username>/migration-kafka-to-dapr:kafka \
  --max-replicas 1 \
  --min-replicas 1
```

## Run sample application using Dapr Binder

Run the following command to build your application as a [Docker image](https://docs.docker.com/engine/reference/commandline/image/) and push it to your [Docker Hub repository](https://docs.docker.com/docker-hub/repos/).
Specify the profile by configuring `-Pdapr` to load `spring-cloud-stream-binder-dapr` dependency and `application-dapr.yml` configuration file.
```shell
mvn clean spring-boot:build-image -Pdapr
```

Now, we need to migrate the kafka configuration to the dapr component by configuring the `Kafka Pub/Sub component` described using the [pubsub.yaml](./cloud-components/kafka-pubsub.yaml) file:
```yaml
componentType: pubsub.kafka
version: v1
metadata:
  - name: brokers # Required. Kafka broker connection setting
    value: "${EVENTHUBS_NAMESPACE_NAME}.servicebus.windows.net:9093"
  - name: authRequired
    value: "true"
  - name: authType # Required.
    value: "password"
  - name: saslUsername
    value: "$ConnectionString"
  - name: saslPassword
    value: "${CONNECTION_STRING}"
```

Run the following command to configure the Dapr component in the Container Apps environment.
```shell
az containerapp env dapr-component set \                                                  
    --name $CONTAINERAPPS_ENVIRONMENT_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --dapr-component-name pubsub \
    --yaml ./cloud-components/kafka-pubsub.yaml
```

> ***NOTE:***
> `--dapr-component-name` must match the `pubsubName` in the `application-dapr.yml` file.

> ***NOTE:***
> If you need to add multiple components, create a separate YAML file for each component and run the `az containerapp env dapr-component set` command multiple times to add each component. For more information about configuring Dapr components, see [Configure Dapr components](https://docs.microsoft.com/azure/container-apps/dapr-overview#configure-dapr-components).

Run the following command to deploy the `Docker image` generated by the current application to `Azure Container Apps`, and enable `Dapr` as a `sidecar` process at the same time.

```shell
az containerapp create \
  --name <CONTAINER_APP_NAME> \
  --resource-group <YOUR_RESOURCE_GROUP_NAME> \
  --environment <YOUR_CONTAINER_APP_ENVIRONMENT_NAME> \
  --image docker.io/moumangtai/migration-kafka-to-dapr:dapr \
  --enable-dapr \
  --dapr-app-id $DAPR_APP_ID \
  --dapr-app-port 9091 \
  --dapr-app-protocol grpc \
  --max-replicas 1 \
  --min-replicas 1
```
This command deploys:
- its accompanying Dapr sidecar configured with `--dapr-app-id $DAPR_APP_ID`、`--dapr-app-protocol grpc` and `--dapr-app-port 9091` for service discovery and invocation.
- its replicas configured with `--max-replicas 1` and `--min-replicas 1`, ensuring that replicas will not be interrupted.

> ***NOTE:***
> For every Dapr app that wants to subscribe to events, create an Event Hubs consumer group with the name of the `dapr-app-id`.
