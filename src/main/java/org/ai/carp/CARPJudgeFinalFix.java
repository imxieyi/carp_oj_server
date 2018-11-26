package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Date;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class CARPJudgeFinalFix {

    private static final Logger logger = LoggerFactory.getLogger(CARPJudgeFinalFix.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CARPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        addLiteCases();
    }

    private static void addLiteCases() {
        Date startTime = new Date(1542964624000L);
        Database.getInstance().getCarpCases().findCARPCasesBySubmitTimeAfter(startTime)
                .stream().filter(CARPCase::isTimedout)
                .forEach(c -> {
                    c.setStatus(CARPCase.WAITING);
                    logger.info(Database.getInstance().getCarpCases().save(c).toString());
                });
    }

}
