// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample.subscriber;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dapr server properties.
 */
@ConfigurationProperties(prefix = "dapr.client")
public class DaprServerProperties {
	private Pubsub pubsub;

	public Pubsub getPubsub() {
		return pubsub;
	}

	public void setPubsub(Pubsub pubsub) {
		this.pubsub = pubsub;
	}

	public static class Pubsub {
		private List<Subscription> subscriptions = new ArrayList<>();

		public void setSubscriptions(List<Subscription> subscriptions) {
			this.subscriptions = subscriptions;
		}

		public List<Subscription> getSubscriptions() {
			return subscriptions;
		}
	}

	public static class Subscription {
		private String pubsubName;
		private String topic;

		public String getPubsubName() {
			return pubsubName;
		}

		public void setPubsubName(String pubsubName) {
			this.pubsubName = pubsubName;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}
	}
}
