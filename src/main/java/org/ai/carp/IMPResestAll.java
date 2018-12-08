package org.ai.carp;

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
public class IMPResestAll {

    private static final Logger logger = LoggerFactory.getLogger(IMPResestAll.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IMPResestAll.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixISE();
        fixIMP();
    }

    private static void fixISE() {
        Database.getInstance().getIseDatasets().findAll().stream()
                .filter(d -> d.getName().contains("NetHEPT"))
                .forEach(d -> {
                    Database.getInstance().getIseCases().findISECasesByDatasetOrderBySubmitTimeDesc(d)
                            .forEach(c -> {
                                c.reset();
                                Database.getInstance().getIseCases().save(c);
                            });
                });
    }

    private static void fixIMP() {
        Database.getInstance().getImpDatasets().findAll().stream()
                .filter(d -> d.getName().contains("NetHEPT"))
                .forEach(d -> {
                    Database.getInstance().getImpCases().findIMPCasesByDatasetOrderBySubmitTimeDesc(d)
                            .forEach(c -> {
                                c.reset();
                                Database.getInstance().getImpCases().save(c);
                            });
                });
    }

}
