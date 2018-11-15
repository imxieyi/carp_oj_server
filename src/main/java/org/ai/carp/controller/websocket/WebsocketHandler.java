package org.ai.carp.controller.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ai.carp.controller.util.CARPUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.runner.JudgePool;
import org.ai.carp.runner.JudgeRunnerWatchdog;
import org.ai.carp.runner.JudgeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Date;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    // Message types
    public static final int WORKER_INFO = 0;
    public static final int WORKER_TICK = 1;
    public static final int CASE_DATA = 2;
    public static final int CASE_START = 3;
    public static final int CASE_RESULT = 4;

    private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private JudgeRunnerWatchdog judgeRunnerWatchdog;

    @Autowired
    private void setJudgeRunnerWatchdog(JudgeRunnerWatchdog judgeRunnerWatchdog) {
        this.judgeRunnerWatchdog = judgeRunnerWatchdog;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message.asBytes());
        int type = rootNode.get("type").asInt();
        String uid = (String) session.getAttributes().get("uid");
        JudgeWorker worker = JudgePool.getInstance().getWorker(uid);
        if (type == WORKER_INFO) {
            String infoUid = rootNode.get("uid").asText();
            if (!uid.equals(infoUid)) {
                logger.error("Wrong worker uid {}, closing session...", infoUid);
                session.close();
                return;
            }
            if (worker == null) {
                worker = new JudgeWorker();
                worker.user = Database.getInstance().getUsers().findById(uid).get();
                JudgePool.getInstance().putWorker(uid, worker);
            }
            worker.session = session;
            worker.maxJobs = rootNode.get("maxTasks").intValue();
            logger.info("Worker {} max tasks: {}", uid, worker.maxJobs);
            return;
        } else {
            if (worker == null) {
                // Worker not registered, resend request
                session.sendMessage(new TextMessage(String.format("{\"type\":%d}", WORKER_INFO)));
                return;
            }
        }
        if (type == WORKER_TICK) {
            // TODO: Regularly check for worker status
            String tickUid = rootNode.get("uid").asText();
            if (!uid.equals(tickUid)) {
                // Wrong worker
                logger.error("Wrong worker uid {}, closing session...", tickUid);
                session.close();
            }
        } else if (type == CASE_START) {
            String cid = rootNode.get("cid").asText();
            if (StringUtils.isEmpty(cid)) {
                return;
            }
            double timestamp = rootNode.get("timestamp").asDouble();
            Date date = new Date((long)(timestamp * 1000d));
            for (CARPCase carpCase : worker.jobs) {
                if (carpCase.getId().equals(cid)) {
                    carpCase.setStatus(CARPCase.RUNNING);
                    carpCase.setJudgeTime(date);
                    CARPCase newCase = Database.getInstance().getCarpCases().save(carpCase);
                    worker.jobs.remove(carpCase);
                    worker.jobs.add(newCase);
                    return;
                }
            }
        } else if (type == CASE_RESULT) {
            String cid = rootNode.get("cid").asText();
            if (StringUtils.isEmpty(cid)) {
                return;
            }
            logger.info("Result returned for {}", cid);
            double timestamp = rootNode.get("timestamp").asDouble();
            for (CARPCase carpCase : worker.jobs) {
                if (carpCase.getId().equals(cid)) {
                    carpCase.setStatus(CARPCase.FINISHED);
                    double judgeTime = (double) carpCase.getJudgeTime().getTime() / 1000d;
                    carpCase.setTime(timestamp - judgeTime);
                    carpCase.setTimedout(rootNode.get("timedout").asBoolean());
                    carpCase.setStdout(rootNode.get("stdout").asText());
                    carpCase.setOutOverflow(rootNode.get("stdout_overflow").asBoolean());
                    carpCase.setStderr(rootNode.get("stderr").asText());
                    carpCase.setErrOverflow(rootNode.get("stderr_overflow").asBoolean());
                    carpCase.setExitcode(rootNode.get("exitcode").asInt());
                    try {
                        CARPUtils.checkResult(carpCase);
                    } catch (Exception e) {
                        logger.error("Error evaluating solution!", e);
                        carpCase.setStatus(CARPCase.ERROR);
                        carpCase.setReason("Error evaluating solution!");
                        carpCase.setCost(0);
                    }
                    Database.getInstance().getCarpCases().save(carpCase);
                    JudgePool.getInstance().removeTask(uid, carpCase);
                    return;
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        InetSocketAddress clientAddress = session.getRemoteAddress();
        logger.info("Accepted ws connection from: {}:{}, uid {}",
                    clientAddress.getAddress(),
                    clientAddress.getPort(),
                    session.getAttributes().get("uid"));
        // Request worker info
        session.sendMessage(new TextMessage(String.format("{\"type\":%d}", WORKER_INFO)));
        session.setTextMessageSizeLimit(1000000);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uid = (String) session.getAttributes().get("uid");
        logger.info("Ws connection closed: {}, uid {}", session.getRemoteAddress().getAddress(), uid);
        JudgePool.getInstance().removeWorker(uid);
        super.afterConnectionClosed(session, status);
    }

    @PostConstruct
    private void startJudgeRunner() throws InterruptedException {
        judgeRunnerWatchdog.start();
    }
}
