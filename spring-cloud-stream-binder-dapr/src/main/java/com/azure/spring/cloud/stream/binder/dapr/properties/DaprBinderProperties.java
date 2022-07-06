// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Dapr binder properties.
 */
@ConfigurationProperties(prefix = "spring.cloud.stream.dapr.binder")
public class DaprBinderProperties {
	/**
	 * Dapr's Sidecar IP for gRPC communication.
	 */
	private String daprIp = "127.0.0.1";

	/**
	 * Dapr's gRPC port.
	 */
	private int daprPort = 50001;

	public String getDaprIp() {
		return daprIp;
	}

	public void setDaprIp(String daprIp) {
		this.daprIp = daprIp;
	}

	public int getDaprPort() {
		return daprPort;
	}

	public void setDaprPort(int daprPort) {
		this.daprPort = daprPort;
	}
}
