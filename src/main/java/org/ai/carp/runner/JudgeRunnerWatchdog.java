package org.ai.carp.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class JudgeRunnerWatchdog {

    private static final Logger logger = LoggerFactory.getLogger(JudgeRunnerWatchdog.class);

    private JudgeRunner judgeRunner;

    @Autowired
    private void setJudgeRunner(JudgeRunner judgeRunner) {
        this.judgeRunner = judgeRunner;
    }

    @Async
    public Future start() throws InterruptedException {
        logger.info("Started judge runner watchdog");
        Future task = judgeRunner.start();
        while (true) {
            Thread.sleep(10000);
            if (task.isDone() || task.isCancelled()) {
                logger.info("Judge runner is down, restarting...");
                task = judgeRunner.start();
            }
        }
    }

}
