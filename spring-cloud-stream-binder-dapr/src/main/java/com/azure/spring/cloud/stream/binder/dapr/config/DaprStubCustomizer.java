// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import io.dapr.v1.DaprGrpc;

@FunctionalInterface
public interface DaprStubCustomizer {
	void customize(DaprGrpc.DaprStub daprStub);
}
