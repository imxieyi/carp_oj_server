package org.ai.carp.runner;

import org.ai.carp.model.judge.CARPCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
                                logger.info("No worker available for judging");
                                JudgePool.getInstance().wait();
                                worker = JudgePool.getInstance().dispatchJob(c.getId(), encodedCase);
                            }
                        }
                        logger.info("Case {} dispatched to worker {}", c.getId(), worker);
                    } catch (IOException e) {
                        logger.error("Case {} is broken", c.getId());
                        // TODO: Handle invalid cases
                    }
                } else {
                    // TODO: Check for dead jobs
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
