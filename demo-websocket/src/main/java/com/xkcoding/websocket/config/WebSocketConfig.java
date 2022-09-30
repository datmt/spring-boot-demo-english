package com.xkcoding.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * <p>
 * WebSocket configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 15:58
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        Registers a /notification endpoint through which the front end connects
        registry.addEndpoint("/notification")
            Resolve cross-domain issues
            .setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        Defines the prefix information of a client subscription address, that is, the prefix information of the message sent by the client receiving the server
        registry.enableSimpleBroker("/topic");
    }

}
