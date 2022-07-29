// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

/**
 * Extended consumer properties for Dapr binder.
 */
public class DaprConsumerProperties {

	/**
	 * the name of the Dapr pub/sub component.
	 */
	private String pubsubName;

	public String getPubsubName() {
		return pubsubName;
	}

	public void setPubsubName(String pubsubName) {
		this.pubsubName = pubsubName;
	}
}
