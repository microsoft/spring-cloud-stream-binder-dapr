// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import java.time.Duration;
import java.util.Map;

/**
 * The Dapr producer binding configuration properties.
 */
public class DaprProducerProperties {

	/**
	 * Send synchronously or asynchronously.
	 */
	private boolean sync;

	/**
	 * the send timeout.
	 */
	private Duration sendTimeout = Duration.ofMillis(10000);

	/**
	 * the name of the message component.
	 */
	private String pubsubName;

	/**
	 * Dapr's Sidecar IP for gRPC communication.
	 */
	private String sidecarIp = "127.0.0.1";

	/**
	 * Dapr's gRPC port.
	 */
	private Integer grpcPort = 50001;

	/**
	 * The content type of the message sent.
	 */
	private String contentType = "application/json";

	/**
	 * Dapr Pub/Sub API metadata.
	 */
	private Map<String, String> metadata;

	/**
	 * Get metadata.
	 *
	 * @return metadata the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Set metadata.
	 *
	 * @param metadata the metadata
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get sync.
	 *
	 * @return sync the sync
	 */
	public boolean isSync() {
		return sync;
	}

	/**
	 * Set sync.
	 *
	 * @param sync the sync
	 */
	public void setSync(boolean sync) {
		this.sync = sync;
	}

	/**
	 * Get pubsub name.
	 *
	 * @return pubsubName the pubsub name
	 */
	public String getPubsubName() {
		return pubsubName;
	}

	/**
	 * Set pubsub name.
	 *
	 * @param pubsubName the pubsub name
	 */
	public void setPubsubName(String pubsubName) {
		this.pubsubName = pubsubName;
	}

	/**
	 * Get sidecar ip.
	 *
	 * @return sidecarIp the sidecar ip
	 */
	public String getSidecarIp() {
		return sidecarIp;
	}

	/**
	 * Set sidecar ip.
	 *
	 * @param sidecarIp the sidecar ip
	 */
	public void setSidecarIp(String sidecarIp) {
		this.sidecarIp = sidecarIp;
	}

	/**
	 * Get grpc port.
	 *
	 * @return grpcPort the grpc port
	 */
	public Integer getGrpcPort() {
		return grpcPort;
	}

	/**
	 * Set grpc port.
	 *
	 * @param grpcPort the grpc port
	 */
	public void setGrpcPort(Integer grpcPort) {
		this.grpcPort = grpcPort;
	}

	/**
	 * Get content type.
	 *
	 * @return contentType the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Set content type.
	 *
	 * @param contentType the content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Get send timeout.
	 *
	 * @return sendTimeout the send timeout
	 */
	public Duration getSendTimeout() {
		return sendTimeout;
	}

	/**
	 * Set send timeout.
	 *
	 * @param sendTimeout the send timeout
	 */
	public void setSendTimeout(Duration sendTimeout) {
		this.sendTimeout = sendTimeout;
	}
}
