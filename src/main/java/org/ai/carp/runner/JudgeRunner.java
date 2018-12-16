package org.ai.carp.runner;

import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.judge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@AutoConfigureAfter({CARPCaseRepository.class, ISECaseRepository.class, IMPCaseRepository.class})
public class JudgeRunner {

    private static final Logger logger = LoggerFactory.getLogger(JudgeRunner.class);

    public static BlockingQueue<BaseCase> queue = new LinkedBlockingQueue<>();

    private static WeakHashMap<String, BaseDataset> datasetCache = new WeakHashMap<>();

    @Async
    public Future start() throws InterruptedException {
        logger.info("Judge runner started");
        try {
            while (true) {
                BaseCase c = queue.poll(5, TimeUnit.SECONDS);
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
                        logger.info("Case {} dispatched to worker {}: {} - {}", c.getId(), worker, c.getUser().getUsername(), c.getBaseDataset().getName());
                    } catch (Exception e) {
                        logger.error("Case {} is broken", c.getId());
                        // TODO: Handle invalid cases
                        c.setStatus(BaseCase.ERROR);
                        c.setReason("Case is broken.");
                        CaseUtils.saveCase(c);
                    }
                } else {
                    // Check for dead jobs
                    List<Integer> finishedStatus = new ArrayList<>();
                    finishedStatus.add(BaseCase.FINISHED);
                    finishedStatus.add(BaseCase.ERROR);
                    List<BaseCase> deadCases = new ArrayList<>();
                    int page = 0;
                    int count;
                    do {
                        List<CARPCase> tmpCases = Database.getInstance().getCarpCases()
                                .findCARPCasesByStatusNotIn(finishedStatus, PageRequest.of(page++, 100));
                        count = tmpCases.size();
                        tmpCases.forEach(cc -> {
                            if (datasetCache.containsKey(cc.getDatasetName())) {
                                cc.setDataset(datasetCache.get(cc.getDatasetName()));
                            } else {
                                datasetCache.put(cc.getDatasetName(), cc.getDataset());
                            }
                        });
                        deadCases.addAll(tmpCases);
                    } while (count == 100);
                    page = 0;
                    do {
                        List<ISECase> tmpCases = Database.getInstance().getIseCases()
                                .findISECasesByStatusNotIn(finishedStatus, PageRequest.of(page++, 100));
                        count = tmpCases.size();
                        tmpCases.forEach(cc -> {
                            if (datasetCache.containsKey(cc.getDatasetName())) {
                                cc.setDataset(datasetCache.get(cc.getDatasetName()));
                            } else {
                                datasetCache.put(cc.getDatasetName(), cc.getDataset());
                            }
                        });
                        deadCases.addAll(tmpCases);
                    } while (count == 100);
                    page = 0;
                    do {
                        List<IMPCase> tmpCases = Database.getInstance().getImpCases()
                                .findIMPCasesByStatusNotIn(finishedStatus, PageRequest.of(page++, 100));
                        count = tmpCases.size();
                        tmpCases.forEach(cc -> {
                            if (datasetCache.containsKey(cc.getDatasetName())) {
                                cc.setDataset(datasetCache.get(cc.getDatasetName()));
                            } else {
                                datasetCache.put(cc.getDatasetName(), cc.getDataset());
                            }
                        });
                        deadCases.addAll(tmpCases);
                    } while (count == 100);
                    if (deadCases.isEmpty()) {
                        continue;
                    }
                    Map<String, BaseCase> deadCasesMap = new HashMap<>();
                    for (BaseCase baseCase : deadCases) {
                        deadCasesMap.put(baseCase.getId(), baseCase);
                    }
                    for (JudgeWorker worker : JudgePool.getInstance().getWorkers()) {
                        for (BaseCase baseCase : worker.jobs) {
                            deadCasesMap.remove(baseCase.getId());
                        }
                    }
                    for (BaseCase inQueue : queue) {
                        deadCasesMap.remove(inQueue.getId());
                    }
                    if (deadCasesMap.size() <= 0) {
                        continue;
                    }
                    for (BaseCase deadCase : deadCasesMap.values()) {
                        deadCase.setStatus(BaseCase.WAITING);
                        BaseCase saved = CaseUtils.saveCase(deadCase);
                        queue.add(saved);
                    }
                    logger.info("Restart {} dead cases.", deadCasesMap.size());
                }
            }
        } catch (RuntimeException e) {
            logger.error("Exception thrown", e);
            throw e;
        }
    }
}
