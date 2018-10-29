package org.ai.carp.runner;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JudgeRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JudgeRunner.class);

    public static BlockingQueue<CARPCase> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        try {
            logger.info("Judge runner started");
            while (true) {
                CARPCase c = queue.poll(10, TimeUnit.SECONDS);
                if (c != null) {
                    // Dispatch job
                    try {
                        String encodedCase = c.getWorkerJson();
                        String worker = JudgePool.getInstance().dispatchJob(c.getId(), encodedCase);
                        synchronized (JudgePool.getInstance()) {
                            while (worker == null) {
                                logger.info("No worker available for judging, {} remains", queue.size() + 1);
                                JudgePool.getInstance().wait(10000);
                                worker = JudgePool.getInstance().dispatchJob(c.getId(), encodedCase);
                            }
                        }
                        logger.info("Case {} dispatched to worker {}", c.getId(), worker);
                    } catch (IOException e) {
                        logger.error("Case {} is broken", c.getId());
                        // TODO: Handle invalid cases
                    }
                } else {
                    // Check for dead jobs
                    List<CARPCase> deadCases = Database.getInstance().getCarpCases()
                            .findCARPCasesByStatusIsNot(CARPCase.FINISHED);
                    if (deadCases.isEmpty()) {
                        continue;
                    }
                    Map<String, CARPCase> deadCasesMap = new HashMap<>();
                    for (CARPCase carpCase : deadCases) {
                        deadCasesMap.put(carpCase.getId(), carpCase);
                    }
                    for (JudgeWorker worker : JudgePool.getInstance().getWorkers()) {
                        for (CARPCase carpCase : worker.jobs) {
                            deadCasesMap.remove(carpCase.getId());
                        }
                    }
                    for (CARPCase inQueue : queue) {
                        deadCasesMap.remove(inQueue.getId());
                    }
                    for (CARPCase deadCase : deadCasesMap.values()) {
                        deadCase.setStatus(CARPCase.WAITING);
                        CARPCase saved = Database.getInstance().getCarpCases().save(deadCase);
                        queue.add(saved);
                    }
                    logger.info("Restart {} dead cases.", deadCasesMap.size());
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
