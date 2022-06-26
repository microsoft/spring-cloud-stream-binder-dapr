// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;


/**
 * The extended Dapr binding configuration properties.
 */
@ConfigurationProperties(prefix = "spring.cloud.stream.dapr")
public class DaprExtendedBindingProperties implements
		ExtendedBindingProperties<DaprConsumerProperties, DaprProducerProperties> {

	private static final String DEFAULTS_PREFIX = "spring.cloud.stream.dapr.default";

	private Map<String, DaprBindingProperties> bindings = new HashMap<>();

	@Override
	public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
		return DaprBindingProperties.class;
	}

	@Override
	public DaprConsumerProperties getExtendedConsumerProperties(String channelName) {
		if (this.bindings.containsKey(channelName)
				&& this.bindings.get(channelName).getConsumer() != null) {
			return this.bindings.get(channelName).getConsumer();
		}
		else {
			return new DaprConsumerProperties();
		}
	}

	@Override
	public DaprProducerProperties getExtendedProducerProperties(String channelName) {
		if (this.bindings.containsKey(channelName)
				&& this.bindings.get(channelName).getProducer() != null) {
			return this.bindings.get(channelName).getProducer();
		}
		else {
			return new DaprProducerProperties();
		}
	}

	@Override
	public Map<String, ?> getBindings() {
		return this.bindings;
	}

	public void setBindings(Map<String, DaprBindingProperties> bindings) {
		this.bindings = bindings;
	}

	@Override
	public String getDefaultsPrefix() {
		return DEFAULTS_PREFIX;
	}

}
