package org.ai.carp;

import org.ai.carp.controller.util.ISEUtils;
import org.ai.carp.model.Database;
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
public class ISEFixBias {

    private static final Logger logger = LoggerFactory.getLogger(ISEFixBias.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ISEFixBias.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixISE();
    }

    private static void fixISE() {
        Database.getInstance().getIseCases().findAll().forEach(c -> {
            c.setValid(false);
            c.setReason(null);
            c.setResult(0);
            ISEUtils.checkResult(c);
            logger.info(Database.getInstance().getIseCases().save(c).toString());
        });
    }

}
