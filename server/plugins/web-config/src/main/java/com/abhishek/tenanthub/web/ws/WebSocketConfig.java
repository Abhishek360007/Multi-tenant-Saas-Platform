package com.abhishek.tenanthub.web.ws;


import com.abhishek.tenanthub.common.consts.WebSocketConst;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;



/**
 * <p>
 * WebSocket Configuration
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.1
 * @date 2021/9/29 21:29
 */


@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    /**
     * Register this endpoint so web pages can connect over WebSocket,
     * i.e. the WebSocket endpoint address; also whether to use SockJS
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * 1. Register /stomp/push as the STOMP endpoint;
         *    once connected, users can communicate over WebSocket, with support for SockJS
         * 2. setAllowedOriginPatterns("*") allows cross-origin
         * 3. withSockJS() enables SockJS
         * 4. addInterceptors Custom interceptor that reads the HTTP session
         * 5. setHandshakeHandler Handshake handler; MyPrincipalHandshakeHandler wraps the authenticated user
         */
        //Client connection address
        registry.addEndpoint(WebSocketConst.STOMP_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Custom scheduler for the heartbeat threads
        var taskScheduler = new ThreadPoolTaskScheduler();
        // Thread pool size for heartbeats
        taskScheduler.setPoolSize(1);
        // Thread name prefix
        taskScheduler.setThreadNamePrefix("ws-heartbeat-");
        // Initialize
        taskScheduler.initialize();
        // Broadcast destinations
        registry.enableSimpleBroker(WebSocketConst.TOPIC, WebSocketConst.QUEUE)
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(taskScheduler);
        // Messages from clients must use the /app prefix
        registry.setApplicationDestinationPrefixes(WebSocketConst.PREFIXES);
        // Prefix for user-specific (one-to-one) messages /user/
        registry.setUserDestinationPrefix(WebSocketConst.USER);
    }


    /**
     * Message transport settings: message size, buffer size, send timeout
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        /*
         * 1. setMessageSizeLimit Message size limit in bytes
         * 2. setSendBufferSizeLimit WebSocket session buffer size in bytes
         * 3. setSendTimeLimit Send timeout in milliseconds
         */
        registration.setMessageSizeLimit(10240)
                .setSendBufferSizeLimit(10240)
                .setSendTimeLimit(10000);
    }


    /**
     * Inbound channel interceptors
     * Inbound channel thread pool (default 1 thread; core size, max size and keep-alive are configurable)
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        /*
         * Message thread pool
         * 1. corePoolSize Core pool size: below this, a new thread is created for each task even if others are idle
         * 2. maxPoolSize Maximum pool size: no new threads are created beyond this
         * 3. keepAliveSeconds Idle time before extra threads are removed, in seconds
         */
        registration.taskExecutor()
                .corePoolSize(8)
                .maxPoolSize(16)
                .queueCapacity(1024)
                .keepAliveSeconds(60);
        registration.interceptors(authChannelInterceptor);
    }


    /**
     * Outbound channel thread pool (default 1 thread; core size, max size and keep-alive are configurable)
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(8)
                .maxPoolSize(16)
                .queueCapacity(1024)
                .keepAliveSeconds(60);
    }


}
