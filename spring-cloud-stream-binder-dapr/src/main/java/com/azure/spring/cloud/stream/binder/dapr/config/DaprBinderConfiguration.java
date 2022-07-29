// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.config;

import java.util.concurrent.TimeUnit;

import com.azure.spring.cloud.stream.binder.dapr.DaprMessageChannelBinder;
import com.azure.spring.cloud.stream.binder.dapr.messaging.DaprMessageConverter;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprBinderConfigurationProperties;
import com.azure.spring.cloud.stream.binder.dapr.properties.DaprExtendedBindingProperties;
import com.azure.spring.cloud.stream.binder.dapr.provisioning.DaprBinderProvisioner;
import com.azure.spring.cloud.stream.binder.dapr.service.DaprGrpcService;
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
@EnableConfigurationProperties({ DaprBinderConfigurationProperties.class, DaprExtendedBindingProperties.class })
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
		PropertyMapper propertyMapper = PropertyMapper.get();
		PropertyMapper.Source<DaprBinderConfigurationProperties.ManagedChannel> from = propertyMapper.from(managedChannelProperties);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getAuthority).whenNonNull().to(builder::overrideAuthority);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getDefaultLoadBalancingPolicy).whenNonNull().to(builder::defaultLoadBalancingPolicy);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxHedgedAttempts).whenNonNull().to(builder::maxHedgedAttempts);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxInboundMessageSize).whenNonNull().to(builder::maxInboundMessageSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxInboundMetadataSize).whenNonNull().to(builder::maxInboundMetadataSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxRetryAttempts).whenNonNull().to(builder::maxRetryAttempts);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getMaxTraceEvents).whenNonNull().to(builder::maxTraceEvents);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getIdleTimeout).whenNonNull().to(x -> builder.idleTimeout(x, TimeUnit.MINUTES));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getKeepAliveTime).whenNonNull().to(x -> builder.keepAliveTime(x, TimeUnit.MINUTES));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getKeepAliveTimeout).whenNonNull().to(x -> builder.keepAliveTimeout(x, TimeUnit.SECONDS));
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getPerRpcBufferLimit).whenNonNull().to(builder::perRpcBufferLimit);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::getRetryBufferSize).whenNonNull().to(builder::retryBufferSize);
		from.as(DaprBinderConfigurationProperties.ManagedChannel::isKeepAliveWithoutCalls).whenNonNull().to(builder::keepAliveWithoutCalls);
		managedChannelBuilderCustomizers.stream().forEach(channelBuilderCustomizer -> channelBuilderCustomizer.customize(builder));
		return builder;
	}

	@Bean
	@ConditionalOnMissingBean
	public ManagedChannel managedChannel(ManagedChannelBuilder managedChannelBuilder) {
		ManagedChannel channel = managedChannelBuilder.build();
		return channel;
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprGrpc.DaprStub daprStub(ManagedChannel channel,
			DaprBinderConfigurationProperties daprBinderProperties,
			ObjectProvider<DaprStubCustomizer> daprStubCustomizers) {
		DaprGrpc.DaprStub daprStub = DaprGrpc.newStub(channel);
		DaprBinderConfigurationProperties.DaprStub daprStubProperties = daprBinderProperties.getDaprStub();
		PropertyMapper propertyMapper = PropertyMapper.get();
		PropertyMapper.Source<DaprBinderConfigurationProperties.DaprStub> from = propertyMapper.from(daprStubProperties);
		from.as(DaprBinderConfigurationProperties.DaprStub::getMaxInboundMessageSize).whenNonNull().to(daprStub::withMaxInboundMessageSize);
		from.as(DaprBinderConfigurationProperties.DaprStub::getMaxOutboundMessageSize).whenNonNull().to(daprStub::withMaxOutboundMessageSize);
		from.as(DaprBinderConfigurationProperties.DaprStub::getCompression).whenNonNull().to(daprStub::withCompression);
		daprStubCustomizers.stream().forEach(daprStubCustomizer -> daprStubCustomizer.customize(daprStub));
		return daprStub;
	}

	@Bean
	@ConditionalOnMissingBean
	public DaprMessageChannelBinder daprMessageChannelBinder(DaprBinderProvisioner daprBinderProvisioner,
			DaprExtendedBindingProperties daprExtendedBindingProperties,
			DaprGrpc.DaprStub daprStub,
			DaprGrpcService daprGrpcService,
			DaprMessageConverter daprMessageConverter) {
		return new DaprMessageChannelBinder(
				null,
				daprBinderProvisioner,
				daprStub,
				daprGrpcService,
				daprExtendedBindingProperties,
				daprMessageConverter);
	}
}
