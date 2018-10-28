package org.ai.carp.controller.websocket;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = serverHttpRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                try {
                    User user = UserUtils.getUser(httpSession, User.WORKER);
                    if (user.getType() == User.WORKER) {
                        // Accept connection
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        logger.info("Refused websocket connection from {}", request.getRemoteAddress().getHostString());
        return false;
    }
}
