package org.ai.carp.runner;

import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.judge.CARPCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JudgePool {

    private static final Logger logger = LoggerFactory.getLogger(JudgePool.class);

    private static JudgePool ourInstance = new JudgePool();

    public static JudgePool getInstance() {
        return ourInstance;
    }

    private Map<String, JudgeWorker> workers = new HashMap<>();

    private JudgePool() {
    }

    public synchronized JudgeWorker getWorker(String id) {
        return workers.get(id);
    }

    public synchronized void putWorker(String id, JudgeWorker worker) {
        workers.put(id, worker);
        notifyAll();
    }

    public synchronized Collection<JudgeWorker> getWorkers() {
        return workers.values();
    }

    public synchronized void removeWorker(String id) {
        JudgeWorker worker = workers.get(id);
        if (worker == null) {
            return;
        }
        for (BaseCase baseCase : worker.jobs) {
            baseCase.setStatus(BaseCase.WAITING);
            CaseUtils.saveCase(baseCase);
            JudgeRunner.queue.add(baseCase);
        }
        workers.remove(id);
    }

    public synchronized void removeTask(String wid, BaseCase baseCase) {
        JudgeWorker worker = workers.get(wid);
        worker.jobs.remove(baseCase);
        notifyAll();
    }

    public synchronized String dispatchJob(String cid, String data) throws IOException {
        for (Map.Entry<String, JudgeWorker> worker : workers.entrySet()) {
            if (worker.getValue().jobs.size() < worker.getValue().maxJobs) {
                worker.getValue().session.sendMessage(new TextMessage(data));
                BaseCase baseCase = CaseUtils.findById(cid);
                if (baseCase == null) {
                    logger.error("WTF! Case {} does not exist!", cid);
                    throw new IOException("Case does not exist!");
                }
                baseCase.setStatus(CARPCase.QUEUED);
                baseCase.setJudgeWorker(worker.getValue().user);
                baseCase = CaseUtils.saveCase(baseCase);
                worker.getValue().jobs.add(baseCase);
                return worker.getKey();
            }
        }
        return null;
    }

}
