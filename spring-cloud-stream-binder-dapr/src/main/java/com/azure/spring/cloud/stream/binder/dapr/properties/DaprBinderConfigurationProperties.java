// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.cloud.stream.binder.dapr.properties;

import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannelBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Dapr binder.
 * The properties in this class are prefixed with <b>spring.cloud.stream.dapr.binder</b>.
 */
@ConfigurationProperties(prefix = "spring.cloud.stream.dapr.binder")
public class DaprBinderConfigurationProperties {
	/**
	 * Dapr's sidecar IP for gRPC communication.
	 */
	private String daprIp = "127.0.0.1";

	/**
	 * Dapr's gRPC port.
	 */
	private int daprPort = 50001;

	/**
	 * Dapr managed channel properties.
	 */
	private ManagedChannel managedChannel = new ManagedChannel();

	/**
	 * Dapr managed channel properties.
	 */
	private DaprStub daprStub = new DaprStub();

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

	public ManagedChannel getManagedChannel() {
		return managedChannel;
	}

	public void setManagedChannel(ManagedChannel managedChannel) {
		this.managedChannel = managedChannel;
	}

	public DaprStub getDaprStub() {
		return daprStub;
	}

	public void setDaprStub(DaprStub daprStub) {
		this.daprStub = daprStub;
	}

	/**
	 * Dapr managed channel properties.
	 */
	public static final class ManagedChannel {

		/**
		 * Decide which connection method to use.
		 */
		private NegotiationType negotiationType = NegotiationType.PLAINTEXT;

		/**
		 * Overrides the authority used with TLS and HTTP virtual hosting. It does not change what host is
		 * actually connected to. Is commonly in the form {@code host:port}.
		 *
		 * <p>If the channel builder overrides authority, any authority override from name resolution
		 * result (via {@link EquivalentAddressGroup#ATTR_AUTHORITY_OVERRIDE}) will be discarded.
		 *
		 * <p>This method is intended for testing, but may safely be used outside of tests as an
		 * alternative to DNS overrides.
		 */
		private String authority;

		/**
		 * Sets the default load-balancing policy that will be used if the service config doesn't specify
		 * one.  If not set, the default will be the "pick_first" policy.
		 *
		 * <p>Policy implementations are looked up in the
		 * {@link LoadBalancerRegistry#getDefaultRegistry default LoadBalancerRegistry}.
		 *
		 * <p>This method is implemented by all stock channel builders that are shipped with gRPC, but may
		 * not be implemented by custom channel builders, in which case this method will throw.
		 *
		 */
		private String defaultLoadBalancingPolicy;

		/**
		 * Sets whether keepalive will be performed when there are no outstanding RPC on a connection.
		 * Defaults to {@code false}.
		 *
		 * <p>Clients must receive permission from the service owner before enabling this option.
		 * Keepalives on unused connections can easilly accidentally consume a considerable amount of
		 * bandwidth and CPU. {@link ManagedChannelBuilder#idleTimeout idleTimeout()} should generally be
		 * used instead of this option.
		 */
		private boolean keepAliveWithoutCalls;

		/**
		 * Sets the retry buffer size in bytes. If the buffer limit is exceeded, no RPC
		 * could retry at the moment, and in hedging case all hedges but one of the same RPC will cancel.
		 * The implementation may only estimate the buffer size being used rather than count the
		 * exact physical memory allocated. The method does not have any effect if retry is disabled by
		 * the client.
		 *
		 * <p>This method may not work as expected for the current release because retry is not fully
		 * implemented yet.
		 */
		private Long retryBufferSize;

		/**
		 * Sets the per RPC buffer limit in bytes used for retry. The RPC is not retriable if its buffer
		 * limit is exceeded. The implementation may only estimate the buffer size being used rather than
		 * count the exact physical memory allocated. It does not have any effect if retry is disabled by
		 * the client.
		 *
		 * <p>This method may not work as expected for the current release because retry is not fully
		 * implemented yet.
		 */
		private Long perRpcBufferLimit;

		/**
		 * Set the duration without ongoing RPCs before going to idle mode.
		 *
		 * <p>In idle mode the channel shuts down all connections, the NameResolver and the
		 * LoadBalancer. A new RPC would take the channel out of idle mode. A channel starts in idle mode.
		 * Defaults to 30 minutes.
		 *
		 * <p>This is an advisory option. Do not rely on any specific behavior related to this option.
		 *
		 */
		private Long idleTimeout;

		/**
		 * Sets the time without read activity before sending a keepalive ping. An unreasonably small
		 * value might be increased, and {@code Long.MAX_VALUE} nano seconds or an unreasonably large
		 * value will disable keepalive. Defaults to infinite.
		 *
		 * <p>Clients must receive permission from the service owner before enabling this option.
		 * Keepalives can increase the load on services and are commonly "invisible" making it hard to
		 * notice when they are causing excessive load. Clients are strongly encouraged to use only as
		 * small of a value as necessary.
		 */
		private Long keepAliveTime;

		/**
		 * Sets the time waiting for read activity after sending a keepalive ping. If the time expires
		 * without any read activity on the connection, the connection is considered dead. An unreasonably
		 * small value might be increased. Defaults to 20 seconds.
		 *
		 * <p>This value should be at least multiple times the RTT to allow for lost packets.
		 */
		private Long keepAliveTimeout;

		/**
		 * Sets the maximum message size allowed to be received on the channel. If not called,
		 * defaults to 4 MiB. The default provides protection to clients who haven't considered the
		 * possibility of receiving large messages while trying to be large enough to not be hit in normal
		 * usage.
		 *
		 * <p>This method is advisory, and implementations may decide to not enforce this.  Currently,
		 * the only known transport to not enforce this is {@code InProcessTransport}.
		 *
		 */
		private Integer maxInboundMessageSize;

		/**
		 * Sets the maximum size of metadata allowed to be received. {@code Integer.MAX_VALUE} disables
		 * the enforcement. The default is implementation-dependent, but is not generally less than 8 KiB
		 * and may be unlimited.
		 *
		 * <p>This is cumulative size of the metadata. The precise calculation is
		 * implementation-dependent, but implementations are encouraged to follow the calculation used for
		 * <a href="http://httpwg.org/specs/rfc7540.html#rfc.section.6.5.2">
		 * HTTP/2's SETTINGS_MAX_HEADER_LIST_SIZE</a>. It sums the bytes from each entry's key and value,
		 * plus 32 bytes of overhead per entry.
		 */
		private Integer maxInboundMetadataSize;

		/**
		 * Sets the maximum number of retry attempts that may be configured by the service config. If the
		 * service config specifies a larger value it will be reduced to this value.  Setting this number
		 * to zero is not effectively the same as {@code disableRetry()} because the former does not
		 * disable
		 * <a
		 * href="https://github.com/grpc/proposal/blob/master/A6-client-retries.md#transparent-retries">
		 * transparent retry</a>.
		 *
		 * <p>This method may not work as expected for the current release because retry is not fully
		 * implemented yet.
		 */
		private Integer maxRetryAttempts;

		/**
		 * Sets the maximum number of hedged attempts that may be configured by the service config. If the
		 * service config specifies a larger value it will be reduced to this value.
		 *
		 * <p>This method may not work as expected for the current release because retry is not fully
		 * implemented yet.
		 */
		private Integer maxHedgedAttempts;

		/**
		 * Sets the maximum number of channel trace events to keep in the tracer for each channel or
		 * subchannel. If set to 0, channel tracing is effectively disabled.
		 */
		private Integer maxTraceEvents;

		public NegotiationType getNegotiationType() {
			return negotiationType;
		}

		public void setNegotiationType(NegotiationType negotiationType) {
			this.negotiationType = negotiationType;
		}

		public String getAuthority() {
			return authority;
		}

		public void setAuthority(String authority) {
			this.authority = authority;
		}

		public String getDefaultLoadBalancingPolicy() {
			return defaultLoadBalancingPolicy;
		}

		public void setDefaultLoadBalancingPolicy(String defaultLoadBalancingPolicy) {
			this.defaultLoadBalancingPolicy = defaultLoadBalancingPolicy;
		}

		public boolean isKeepAliveWithoutCalls() {
			return keepAliveWithoutCalls;
		}

		public void setKeepAliveWithoutCalls(boolean keepAliveWithoutCalls) {
			this.keepAliveWithoutCalls = keepAliveWithoutCalls;
		}

		public Long getRetryBufferSize() {
			return retryBufferSize;
		}

		public void setRetryBufferSize(Long retryBufferSize) {
			this.retryBufferSize = retryBufferSize;
		}

		public Long getPerRpcBufferLimit() {
			return perRpcBufferLimit;
		}

		public void setPerRpcBufferLimit(Long perRpcBufferLimit) {
			this.perRpcBufferLimit = perRpcBufferLimit;
		}

		public Long getIdleTimeout() {
			return idleTimeout;
		}

		public void setIdleTimeout(Long idleTimeout) {
			this.idleTimeout = idleTimeout;
		}

		public Long getKeepAliveTime() {
			return keepAliveTime;
		}

		public void setKeepAliveTime(Long keepAliveTime) {
			this.keepAliveTime = keepAliveTime;
		}

		public Long getKeepAliveTimeout() {
			return keepAliveTimeout;
		}

		public void setKeepAliveTimeout(Long keepAliveTimeout) {
			this.keepAliveTimeout = keepAliveTimeout;
		}

		public Integer getMaxInboundMessageSize() {
			return maxInboundMessageSize;
		}

		public void setMaxInboundMessageSize(Integer maxInboundMessageSize) {
			this.maxInboundMessageSize = maxInboundMessageSize;
		}

		public Integer getMaxInboundMetadataSize() {
			return maxInboundMetadataSize;
		}

		public void setMaxInboundMetadataSize(Integer maxInboundMetadataSize) {
			this.maxInboundMetadataSize = maxInboundMetadataSize;
		}

		public Integer getMaxRetryAttempts() {
			return maxRetryAttempts;
		}

		public void setMaxRetryAttempts(Integer maxRetryAttempts) {
			this.maxRetryAttempts = maxRetryAttempts;
		}

		public Integer getMaxHedgedAttempts() {
			return maxHedgedAttempts;
		}

		public void setMaxHedgedAttempts(Integer maxHedgedAttempts) {
			this.maxHedgedAttempts = maxHedgedAttempts;
		}

		public Integer getMaxTraceEvents() {
			return maxTraceEvents;
		}

		public void setMaxTraceEvents(Integer maxTraceEvents) {
			this.maxTraceEvents = maxTraceEvents;
		}
	}

	/**
	 * Dapr stub properties.
	 */
	public static final class DaprStub {
		/**
		 * Limits the maximum acceptable message size from a remote peer.
		 *
		 * <p>If unset, the {@link ManagedChannel#maxInboundMessageSize} limit is used.
		 */
		private Integer maxInboundMessageSize;

		/**
		 * Limits the maximum acceptable message size to send a remote peer.
		 */
		private Integer maxOutboundMessageSize;

		/**
		 *  Set's the compressor name to use for the call.  It is the responsibility of the application
		 *  to make sure the server supports decoding the compressor picked by the client.  To be clear,
		 *  this is the compressor used by the stub to compress messages to the server.  To get
		 *  compressed responses from the server, set the appropriate {@link io.grpc.DecompressorRegistry}
		 *  on the {@link io.grpc.ManagedChannelBuilder}.
		 */
		private String compression;

		public Integer getMaxInboundMessageSize() {
			return maxInboundMessageSize;
		}

		public void setMaxInboundMessageSize(Integer maxInboundMessageSize) {
			this.maxInboundMessageSize = maxInboundMessageSize;
		}

		public Integer getMaxOutboundMessageSize() {
			return maxOutboundMessageSize;
		}

		void setMaxOutboundMessageSize(Integer maxOutboundMessageSize) {
			this.maxOutboundMessageSize = maxOutboundMessageSize;
		}

		public String getCompression() {
			return compression;
		}

		public void setCompression(String compression) {
			this.compression = compression;
		}
	}

	public enum NegotiationType {
		/**
		 * Makes the client use TLS.
		 */
		TLS,
		/**
		 * Use of a plaintext connection to the server. By default a secure connection mechanism
		 * such as TLS will be used.
		 *
		 * <p>Should only be used for testing or for APIs where the use of such API or the data
		 * exchanged is not sensitive.
		 *
		 * <p>This assumes prior knowledge that the target of this channel is using plaintext.  It will
		 * not perform HTTP/1.1 upgrades.
		 */
		PLAINTEXT
	}
}
