package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.LiteCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class LiteCaseFix {

    private static final Logger logger = LoggerFactory.getLogger(LiteCaseFix.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LiteCaseFix.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixLite();
    }

    private static void fixLite() {
        Database.getInstance().getLiteCases().deleteAll();
        Database.getInstance().getCarpCases().findAll()
                .forEach(d -> logger.info(Database.getInstance().getLiteCases().insert(new LiteCase(d)).toString()));
        Database.getInstance().getIseCases().findAll()
                .forEach(d -> logger.info(Database.getInstance().getLiteCases().insert(new LiteCase(d)).toString()));
        Database.getInstance().getImpCases().findAll()
                .forEach(d -> logger.info(Database.getInstance().getLiteCases().insert(new LiteCase(d)).toString()));
    }

}
