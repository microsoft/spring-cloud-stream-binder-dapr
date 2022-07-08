// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import io.grpc.ManagedChannelBuilder;

@FunctionalInterface
public interface ManagedChannelBuilderCustomizer {
	void customize(ManagedChannelBuilder managedChannelBuilder);
}
