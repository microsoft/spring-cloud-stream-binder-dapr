// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.sample.dapr;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dapr server properties
 */
@ConfigurationProperties(prefix = "dapr.client")
@Data
public class DaprServerProperties {
    private Pubsub pubsub;

    @Data
    public static class Pubsub{
        private final List<Subscription> subscriptions = new ArrayList<>();
    }

    @Data
    public static class Subscription{
        private String pubsubName;
        private String topic;
    }
}
