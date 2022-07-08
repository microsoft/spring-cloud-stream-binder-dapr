// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import java.util.concurrent.TimeUnit;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprBinderConfigurationProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import io.dapr.v1.DaprGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The auto-configuration for Dapr binder.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties({DaprBinderConfigurationProperties.class, DaprExtendedBindingProperties.class})
public class DaprBinderConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public DaprBinderProvisioner daprBinderProvisioner() {
		return new DaprBinderProvisioner();
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageConverter daprMessageConverter() {
		return new DaprMessageConverter();
	}

	@Bean
	@ConditionalOnMissingBean
	public ManagedChannelBuilder managedChannelBuilder(DaprBinderConfigurationProperties daprBinderProperties,
			ObjectProvider<ManagedChannelBuilderCustomizer> managedChannelBuilderCustomizers) {
		ManagedChannelBuilder builder = ManagedChannelBuilder.forAddress(daprBinderProperties.getDaprIp(), daprBinderProperties.getDaprPort());
		DaprBinderConfigurationProperties.ManagedChannel managedChannelProperties = daprBinderProperties.getManagedChannel();
		DaprBinderConfigurationProperties.NegotiationType negotiationType = managedChannelProperties.getNegotiationType();
		if (negotiationType.equals(DaprBinderConfigurationProperties.NegotiationType.PLAINTEXT)) {
			builder.usePlaintext();
		}
		else if (negotiationType.equals(DaprBinderConfigurationProperties.NegotiationType.TLS)) {
			builder.useTransportSecurity();
		}
		PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		PropertyMapper.Source<DaprBinderConfigurationProperties.ManagedChannel> from = propertyMapper.from(managedChannelProperties);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getAuthority).whenNonNull().to(builder::overrideAuthority);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getDefaultLoadBalancingPolicy).whenNonNull().to(builder::defaultLoadBalancingPolicy);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getDefaultLoadBalancingPolicy).whenNonNull().to(builder::defaultLoadBalancingPolicy);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxHedgedAttempts).when((x) -> x > 0).to(builder::maxHedgedAttempts);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxInboundMessageSize).when((x) -> x > 0).to(builder::maxInboundMessageSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxInboundMetadataSize).when((x) -> x > 0).to(builder::maxInboundMetadataSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxRetryAttempts).when((x) -> x > 0).to(builder::maxRetryAttempts);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxTraceEvents).when((x) -> x > 0).to(builder::maxTraceEvents);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getIdleTimeout).when((x) -> x > 0).to(x -> builder.idleTimeout(x, TimeUnit.MINUTES));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getKeepAliveTime).when((x) -> x > 0).to(x -> builder.keepAliveTime(x, TimeUnit.MINUTES));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getKeepAliveTimeout).when((x) -> x > 0).to(x -> builder.keepAliveTimeout(x, TimeUnit.SECONDS));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getPerRpcBufferLimit).when((x) -> x > 0).to(builder::perRpcBufferLimit);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getRetryBufferSize).when((x) -> x > 0).to(builder::retryBufferSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::isKeepAliveWithoutCalls).whenTrue().to(builder::keepAliveWithoutCalls);
		managedChannelBuilderCustomizers.stream().forEach(channelBuilderCustomizer -> channelBuilderCustomizer.customize(builder));
		return builder;
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprGrpc.DaprStub daprStub(ManagedChannelBuilder managedChannelBuilder) {
		ManagedChannel channel = managedChannelBuilder.build();
		return DaprGrpc.newStub(channel);
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageChannelBinder daprMessageChannelBinder(DaprBinderProvisioner daprBinderProvisioner,
			DaprExtendedBindingProperties daprExtendedBindingProperties,
			DaprGrpc.DaprStub daprStub,
			DaprMessageConverter daprMessageConverter) {
		return new DaprMessageChannelBinder(
				null,
				daprBinderProvisioner,
				daprStub,
				daprExtendedBindingProperties,
				daprMessageConverter);
	}
}
