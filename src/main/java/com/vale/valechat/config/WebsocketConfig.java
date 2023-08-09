package com.vale.valechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket and Stomp configuration
 */
@Configuration
// Let the program identify the request sent by the exposed stomp node and use it with the Controller @ MessageMapping
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Expose nodes that let clients connect to the websocket, there can be multiple, such as privateServer and publicServer
//        registry.addEndpoint("/privateChat").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // The client accesses the server with the/app prefix, and these messages with/app are matched to the @MessageMapping annotation method
        // (the client requests messages to the server for use)
        registry.setApplicationDestinationPrefixes("/app");
        // On the exposed node, the message broker will process the request message with the prefix/topic and/queue
        // (the server uses the request to send messages to the client).
        registry.enableSimpleBroker("/topic", "/user", "/online");
        // The server specifies one-to-one push messages to users. When using the sendToUser method, the/user will be spliced by default
        // so the client must subscribe to the topic of the relevant/user prefix before it can be accepted normally.

        // '/user' has been automatically spliced in simpMessagingTemplate.convertAndSendToUser
//        registry.setUserDestinationPrefix("/user");
    }
}
