// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample.subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.protobuf.Empty;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.DaprAppCallbackProtos;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Automatically configures and runs the gRPC server with @GrpcService implementations.
 */
@GrpcService
public class DaprSpringService extends AppCallbackGrpc.AppCallbackImplBase implements InitializingBean,
		ApplicationEventPublisherAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaprSpringService.class);
	private final DaprServerProperties properties;
	private final List<DaprAppCallbackProtos.TopicSubscription> topicSubscriptionList = new ArrayList<>();
	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Construct a {@link DaprSpringService} with the specified {@link DaprServerProperties}.
	 *
	 * @param properties the dapr server properties
	 */
	public DaprSpringService(DaprServerProperties properties) {
		this.properties = properties;
	}

	@Override
	public void afterPropertiesSet() {
		DaprServerProperties.Pubsub pubsub = properties.getPubsub();
		if (pubsub != null && !pubsub.getSubscriptions().isEmpty()) {
			pubsub.getSubscriptions().stream().map(SUBSCRIPTION_TOPIC_SUBSCRIPTION_FUNCTION)
					.forEach(topicSubscriptionList::add);
		}
	}

	private static final Function<DaprServerProperties.Subscription, DaprAppCallbackProtos.TopicSubscription> SUBSCRIPTION_TOPIC_SUBSCRIPTION_FUNCTION = (subscription) -> {
		return DaprAppCallbackProtos.TopicSubscription
				.newBuilder()
				.setPubsubName(subscription.getPubsubName())
				.setTopic(subscription.getTopic())
				.build();
	};

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
			LOGGER.info("------onTopicEvent------");
			LOGGER.info("TopicEventRequest : \n {}", request);
			DaprAppCallbackProtos.TopicEventResponse response =
					DaprAppCallbackProtos.TopicEventResponse.newBuilder()
							.setStatus(DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus.SUCCESS)
							.build();
			responseObserver.onNext(response);
			applicationEventPublisher.publishEvent(request);
		}
		catch (Throwable e) {
			responseObserver.onError(e);
		}
		finally {
			responseObserver.onCompleted();
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
}
