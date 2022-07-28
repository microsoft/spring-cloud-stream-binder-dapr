// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.protobuf.Empty;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.DaprAppCallbackProtos;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class DaprGrpcService extends AppCallbackGrpc.AppCallbackImplBase {
	private final List<DaprAppCallbackProtos.TopicSubscription> topicSubscriptionList = new ArrayList<>();
	private final List<Consumer<DaprAppCallbackProtos.TopicEventRequest>> consumers = new ArrayList<>();

	@Override
	public void listTopicSubscriptions(Empty request,
			StreamObserver<DaprAppCallbackProtos.ListTopicSubscriptionsResponse> responseObserver) {
		try {
			DaprAppCallbackProtos.ListTopicSubscriptionsResponse.Builder builder =
					DaprAppCallbackProtos.ListTopicSubscriptionsResponse.newBuilder();
			topicSubscriptionList.forEach(builder::addSubscriptions);
			DaprAppCallbackProtos.ListTopicSubscriptionsResponse response = builder.build();
			responseObserver.onNext(response);
		}
		catch (Throwable e) {
			responseObserver.onError(e);
		}
		finally {
			responseObserver.onCompleted();
		}
	}

	@Override
	public void onTopicEvent(DaprAppCallbackProtos.TopicEventRequest request,
			StreamObserver<DaprAppCallbackProtos.TopicEventResponse> responseObserver) {
		try {
			consumers.forEach(consumer -> consumer.accept(request));
			DaprAppCallbackProtos.TopicEventResponse response =
					DaprAppCallbackProtos.TopicEventResponse.newBuilder()
							.setStatus(DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus.SUCCESS)
							.build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
		catch (Throwable e) {
			responseObserver.onError(e);
		}
	}

	public void registerConsumer(String pubsubName, String topic, Consumer<DaprAppCallbackProtos.TopicEventRequest> consumer) {
		topicSubscriptionList.add(DaprAppCallbackProtos.TopicSubscription
				.newBuilder()
				.setPubsubName(pubsubName)
				.setTopic(topic)
				.build());
		consumers.add(consumer);
	}
}
