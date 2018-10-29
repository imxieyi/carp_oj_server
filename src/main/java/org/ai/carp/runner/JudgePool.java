package org.ai.carp.runner;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        for (CARPCase carpCase : worker.jobs) {
            carpCase.setStatus(CARPCase.WAITING);
            Database.getInstance().getCarpCases().save(carpCase);
            JudgeRunner.queue.add(carpCase);
        }
        workers.remove(id);
    }

    public synchronized void removeTask(String wid, CARPCase carpCase) {
        JudgeWorker worker = workers.get(wid);
        worker.jobs.remove(carpCase);
        notifyAll();
    }

    public synchronized String dispatchJob(String cid, String data) throws IOException {
        for (Map.Entry<String, JudgeWorker> worker : workers.entrySet()) {
            if (worker.getValue().jobs.size() < worker.getValue().maxJobs) {
                worker.getValue().session.sendMessage(new TextMessage(data));
                Optional<CARPCase> optionalCARPCase = Database.getInstance().getCarpCases().findById(cid);
                if (!optionalCARPCase.isPresent()) {
                    logger.error("WTF! Case {} does not exist!", cid);
                    throw new IOException("Case does not exist!");
                }
                CARPCase carpCase = optionalCARPCase.get();
                carpCase.setStatus(CARPCase.QUEUED);
                carpCase.setJudgeWorker(worker.getValue().user);
                carpCase = Database.getInstance().getCarpCases().save(carpCase);
                worker.getValue().jobs.add(carpCase);
                return worker.getKey();
            }
        }
        return null;
    }

}
