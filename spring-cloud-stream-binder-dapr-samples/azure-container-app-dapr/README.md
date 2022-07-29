# AZURE CONTAINER APP DAPR SAMPLE

In this example, you deploy a web service app to [Azure Container Apps](https://docs.microsoft.com/azure/container-apps/overview).
And the app demonstrates how to use `Spring Cloud Stream Dapr Binder` to send messages to `Azure Event Hubs` and receive messages from `Azure Event Hubs`.

## Prerequisites
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
- [Azure Event Hubs](https://docs.microsoft.com/azure/event-hubs/event-hubs-quickstart-cli)
- [Azure Storage Account](https://docs.microsoft.com/azure/storage/common/storage-account-create?tabs=azure-portal)
- [Azure subscription](https://azure.microsoft.com/free/)
- [Docker](https://www.docker.com/get-started/)

## 1. Create an Azure Container Apps environment for your container apps

Individual container apps are deployed to a single [Azure Container Apps environment](https://docs.microsoft.com/azure/container-apps/environment), which acts as a secure boundary around groups of container apps.
So we first take the following steps to create an [Azure Container Apps environment](https://docs.microsoft.com/azure/container-apps/environment) for your container application.
### 1.1. Set up
First, sign in to Azure from the CLI. Run the following command, and follow the prompts to complete the authentication process.
```shell
az login
```
Set the current subscription context. Replace `MyAzureSub` with the name of the Azure subscription you want to use:
```shell
az account set --subscription <MyAzureSub>
```
Next, install the Azure Container Apps extension for the CLI.
```shell
az extension add --name containerapp --upgrade
```
Now that the extension is installed, register the `Microsoft.App` namespace.
```shell
az provider register --namespace Microsoft.App
```

> ***NOTE:***
> Azure Container Apps resources have migrated from the `Microsoft.Web` namespace to the `Microsoft.App` namespace. Refer to [Namespace migration from Microsoft.Web to Microsoft.App in March 2022](https://github.com/enterprises/microsoftopensource/saml/initiate?return_to=https%3A%2F%2Fgithub.com%2Fmicrosoft%2Fazure-container-apps%2Fissues%2F109) for more details.

Next, set the following environment variables:
```shell
# Name of the new resource group.
RESOURCE_GROUP_NAME="dapr-resource-group"
# Location. Values from: `az account list-locations`.
LOCATION="canadacentral"
# Name of the Container Apps environment.
CONTAINERAPPS_ENVIRONMENT_NAME="dapr-containerapps-environment""
```
With these variables defined, you can create a resource group to organize the services related to your new container app.

### 1.2. Create a resource group
```shell
az group create \
  --name $RESOURCE_GROUP_NAME \
  --location $LOCATION
```

With the CLI upgraded and a new resource group available, you can create a Container Apps environment.

### 1.3. Create an Azure Container Apps environment

To create the environment, run the following command:

```shell
az containerapp env create \
  --name $CONTAINERAPPS_ENVIRONMENT_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --location "$LOCATION"
```


## 2. Configure the Pub/Sub component to connect to Azure Event Hubs
`Dapr` integrates with `Pub/Sub` message buses to provide applications with the ability to create event-driven, loosely coupled architectures where producers send events to consumers via topics.

`Dapr` supports the configuration of multiple, named, `Pub/Sub components` per application. Each `Pub/Sub component` has a name and this name is used when publishing a message topic.

`Pub/Sub components` are extensible. A list of support `Pub/Sub components` is [here](https://docs.dapr.io/reference/components-reference/supported-pubsub/) and the implementations can be found in the [components-contrib repo](https://github.com/dapr/components-contrib).

In this example, we configure the `Azure Event Hubs Pub/Sub component` described using the [pubsub.yaml](./cloud-components/pubsub.yaml) file:
```yaml
componentType: pubsub.azure.eventhubs
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
grpc:
  server:
    port: 9091
```
Replace the `<AZURE_EVENTHUB_NAME>` and `<PUBSUB_COMPONENT_NAME>`in the [application.yaml](./src/main/resources/application.yaml).

> ***NOTE:***
> `grpc.server.port` specifies the port (default 9090) that grpc server starts. But port 9090 is already occupied by `Azure Container Apps`, so it needs to be changed to 9091.

Run the following command to configure the Dapr component in the Container Apps environment.
```
az containerapp env dapr-component set \
    --name $CONTAINERAPPS_ENVIRONMENT_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --dapr-component-name eventhubs-pubsub \
    --yaml ./cloud-components/pubsub.yaml
```
> ***NOTE:***
> `--dapr-component-name` must match the `pubsubName` in the `application-dapr.yml` file.

> ***NOTE:***
> If you need to add multiple components, create a separate YAML file for each component and run the `az containerapp env dapr-component set` command multiple times to add each component. For more information about configuring Dapr components, see [Configure Dapr components](https://docs.microsoft.com/azure/container-apps/dapr-overview#configure-dapr-components).

## 3. Build and push a Docker image to your Docker Hub repository
Build the current project as a [Docker image](https://docs.docker.com/engine/reference/commandline/image/) and push it to your [Docker Hub repository](https://docs.docker.com/docker-hub/repos/).

Update the `<properties>` collection in the [pom.xml](./pom.xml) file.
Your free [Docker Account](https://docs.docker.com/docker-id/) grants you access to `Docker Hub repository`.
```xml
<properties>
	<docker.account.username>username</docker.account.username>
	<docker.account.password>password</docker.account.password>
</properties>
```

Run the following command to build and push the image to your `Docker Hub registry`.
```shell
mvn spring-boot:build-image
```

The build and push are complete when the following output appears in the console.

```shell
Successfully built image 'docker.io/<docker.account.username>/azure-container-app-dapr:latest'
...
Pushed image 'docker.io/<docker.account.username>/azure-container-app-dapr:latest'
```

## 4. Deploy the application (HTTP web server)
Set the Azure Container variable:
```shell
# Name of the new Azure Container App.
CONTAINER_APP_NAME="dapr-app-container"
# The Dapr application identifier.
DAPR_APP_ID="dapr-sidecar"
```

Run the following command to deploy the `Docker image` generated by the current application to `Azure Container Apps`, and enable `Dapr` as a `sidecar` process at the same time.

```shell
az containerapp create \
  --name $CONTAINER_APP_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --environment $CONTAINERAPPS_ENVIRONMENT_NAME \
  --image docker.io/<docker.account.username>/azure-container-app-dapr:latest \
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

## 5. Verify the result
Once the deployment succeeds, it will display the container's fully qualified domain name (FQDN).

```shell
"fqdn": "dapr-app.calmwater-80336c7e.canadacentral.azurecontainerapps.io",
```

Run the following commands to send the specified message:

```shell
curl -X POST https://dapr-app.calmwater-80336c7e.canadacentral.azurecontainerapps.io/send?message=hello
```

Data logged via a container app are stored in the `ContainerAppConsoleLogs_CL` custom table in the Log Analytics workspace. You can view logs through the Azure portal or with the CLI. Wait a few minutes for the analytics to arrive for the first time before you're able to query the logged data.

Use the following CLI command to view logs on the command line.

```shell
LOG_ANALYTICS_WORKSPACE_CLIENT_ID=`az containerapp env show --name $CONTAINERAPPS_ENVIRONMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.appLogsConfiguration.logAnalyticsConfiguration.customerId --out tsv`

az monitor log-analytics query \
  --workspace $LOG_ANALYTICS_WORKSPACE_CLIENT_ID \
  --analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == 'dapr-app-container' | project ContainerAppName_s, Log_s, TimeGenerated"\
  --out table
```

## 6.Clean up resources
Once you're done, run the following command to delete your resource group along with all the resources you created in this tutorial.

```shell
az group delete \
    --resource-group $RESOURCE_GROUP_NAME
```

This command deletes the resource group that includes all of the resources created in this tutorial.
