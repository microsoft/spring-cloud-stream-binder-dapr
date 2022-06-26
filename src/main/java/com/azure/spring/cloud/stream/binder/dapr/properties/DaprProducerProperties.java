// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

/**
 * The Dapr producer binding configuration properties.
 */
public class DaprProducerProperties {

	private boolean sync;

	private long sendTimeout = 10000;

	private String pubsubName;

	private String sidecarIp = "127.0.0.1";

	private Integer grpcPort = 50001;

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public long getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(long sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public String getPubsubName() {
		return pubsubName;
	}

	public void setPubsubName(String pubsubName) {
		this.pubsubName = pubsubName;
	}

	public String getSidecarIp() {
		return sidecarIp;
	}

	public void setSidecarIp(String sidecarIp) {
		this.sidecarIp = sidecarIp;
	}

	public Integer getGrpcPort() {
		return grpcPort;
	}

	public void setGrpcPort(Integer grpcPort) {
		this.grpcPort = grpcPort;
	}
}
