package org.ai.carp.config;

import org.ai.carp.controller.websocket.HandshakeInterceptor;
import org.ai.carp.controller.websocket.WebsocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebsocketHandler(), "/api/websocket")
                .addInterceptors(new HandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
