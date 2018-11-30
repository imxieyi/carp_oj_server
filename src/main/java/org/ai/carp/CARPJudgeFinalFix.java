package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.LiteCase;
import org.ai.carp.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.*;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class CARPJudgeFinalFix {

    private static final Logger logger = LoggerFactory.getLogger(CARPJudgeFinalFix.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CARPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
//        fixForceQuit();
//        restartTimedout();
        selectLastValid();
    }

    private static void fixForceQuit() {
        Date startTime = new Date(1542964624000L);
        Database.getInstance().getCarpCases().findCARPCasesBySubmitTimeAfter(startTime)
                .stream().filter(c -> c.getExitcode() == 137)
                .forEach(c -> {
                    c.setStatus(CARPCase.WAITING);
                    logger.info(Database.getInstance().getCarpCases().save(c).toString());
                });
    }

    private static void restartTimedout() {
        Date startTime = new Date(1542964624000L);
        Database.getInstance().getCarpCases().findCARPCasesBySubmitTimeAfter(startTime)
                .stream().filter(CARPCase::isTimedout)
                .forEach(c -> {
                    c.setStatus(CARPCase.WAITING);
                    logger.info(Database.getInstance().getCarpCases().save(c).toString());
                });
    }

    private static void selectLastValid() {
        String[] userNames = {  };
        Date endTime = new Date(1542964624000L);
        // Query datasets
        List<CARPDataset> datasets = Database.getInstance().getCarpDatasets().findAll()
                .stream().filter(CARPDataset::isFinalJudge).collect(Collectors.toList());
        // Query users
        List<User> users = Arrays.stream(userNames).map(u ->
                Database.getInstance().getUsers().findByUsername(u)).collect(Collectors.toList());
        List<CARPCase> cases = new ArrayList<>();
        for (User u : users) {
            CARPCase submission = Database.getInstance().getCarpCases()
                    .findFirstByUserAndValidAndSubmitTimeBeforeOrderBySubmitTimeDesc(u, true, endTime);
            if (submission == null || submission.getArchive() == null) {
                continue;
            }
            for (CARPDataset dataset : datasets) {
                // Remove old cases
                List<CARPCase> oldCases = Database.getInstance().getCarpCases()
                        .findCARPCasesByUserAndDatasetOrderBySubmitTimeDesc(u, dataset);
                for (CARPCase c : oldCases) {
                    logger.info("Removed: {}", c.toString());
                    Database.getInstance().getCarpCases().delete(c);
                }
                for (int i=0; i<5; i++) {
                    cases.add(new CARPCase(u, dataset, submission.getArchive()));
                }
            }
        }
        Collections.shuffle(cases);
        for (CARPCase c : cases) {
            CARPCase newC = Database.getInstance().getCarpCases().insert(c);
            Database.getInstance().getLiteCases().insert(new LiteCase(c));
            logger.info(newC.toString());
        }
    }

}
