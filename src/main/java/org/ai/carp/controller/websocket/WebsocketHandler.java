package org.ai.carp.controller.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ai.carp.controller.util.CARPUtils;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.ISEUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.IMPCase;
import org.ai.carp.model.judge.ISECase;
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
    public static final int CASE_ERROR = 5;

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
            for (BaseCase baseCase : worker.jobs) {
                if (baseCase.getId().equals(cid)) {
                    baseCase.setStatus(CARPCase.RUNNING);
                    baseCase.setJudgeTime(date);
                    BaseCase newCase = CaseUtils.saveCase(baseCase);
                    worker.jobs.remove(baseCase);
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
            for (BaseCase baseCase : worker.jobs) {
                if (baseCase.getId().equals(cid)) {
                    baseCase.setStatus(CARPCase.FINISHED);
                    double judgeTime = (double) baseCase.getJudgeTime().getTime() / 1000d;
                    baseCase.setTime(timestamp - judgeTime);
                    baseCase.setTimedout(rootNode.get("timedout").asBoolean());
                    baseCase.setStdout(rootNode.get("stdout").asText());
                    baseCase.setOutOverflow(rootNode.get("stdout_overflow").asBoolean());
                    baseCase.setStderr(rootNode.get("stderr").asText());
                    baseCase.setErrOverflow(rootNode.get("stderr_overflow").asBoolean());
                    baseCase.setExitcode(rootNode.get("exitcode").asInt());
                    try {
                        switch (baseCase.getType()) {
                            case BaseDataset.CARP:
                                CARPUtils.checkResult((CARPCase)baseCase);
                                break;
                            case BaseDataset.ISE:
                                ISEUtils.checkResult((ISECase)baseCase);
                                break;
                            case BaseDataset.IMP:
                                baseCase.setValid(rootNode.get("valid").asBoolean());
                                baseCase.setReason(rootNode.get("reason").asText());
                                ((IMPCase)baseCase).setInfluence(rootNode.get("influence").asDouble());
                                break;
                            default:
                                logger.error("Invalid case type: {}", baseCase.getType());
                        }
                    } catch (Exception e) {
                        logger.error("Error evaluating solution!", e);
                        baseCase.setStatus(BaseCase.ERROR);
                        baseCase.setReason("Error evaluating solution!");
                    }
                    CaseUtils.saveCase(baseCase);
                    JudgePool.getInstance().removeTask(uid, baseCase);
                    return;
                }
            }
        } else if (type == CASE_ERROR) {
            String cid = rootNode.get("cid").asText();
            if (StringUtils.isEmpty(cid)) {
                return;
            }
            logger.info("Result returned for {}", cid);
            for (BaseCase baseCase : worker.jobs) {
                if (baseCase.getId().equals(cid)) {
                    baseCase.setStatus(CARPCase.ERROR);
                    baseCase.setReason(rootNode.get("message").asText());
                    CaseUtils.saveCase(baseCase);
                    JudgePool.getInstance().removeTask(uid, baseCase);
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
