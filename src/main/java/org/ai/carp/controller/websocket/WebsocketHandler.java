package org.ai.carp.controller.websocket;

import org.ai.carp.runner.JudgeRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private Thread judgeRunnerThread;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readTree(message.asBytes());
//        int type = rootNode.get("type").asInt();
        session.sendMessage(new TextMessage("hello"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        logger.info("Accepted ws connection from: {}:{}", clientAddress.getAddress(), clientAddress.getPort());
        sessions.add(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Ws connection closed: {}", session.getRemoteAddress().getAddress());
        super.afterConnectionClosed(session, status);
    }

    @PostConstruct
    private void startJudgeRunner() {
        logger.info("Starting judge runner");
        judgeRunnerThread = new Thread(new JudgeRunner());
        judgeRunnerThread.start();
    }
}
