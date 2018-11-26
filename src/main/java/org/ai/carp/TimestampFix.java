package org.ai.carp;

import org.ai.carp.model.Database;
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
public class TimestampFix {

    private static final Logger logger = LoggerFactory.getLogger(TimestampFix.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TimestampFix.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixTimestamps();
    }

    private static void fixTimestamps() {
        Database.getInstance().getCarpCases().findAll()
                .stream().filter(c -> c.getJudgeTime() != null)
                .filter(c -> c.getJudgeWorker().getUsername().equals("judge")).forEach(c -> {
                    c.setJudgeTime(new Date(c.getJudgeTime().getTime() - 28751000L));
                    logger.info(Database.getInstance().getCarpCases().save(c).toString());
        });
    }

}
