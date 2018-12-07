package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.judge.BaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Scanner;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class IMPDatasetFix {

    private static final Logger logger = LoggerFactory.getLogger(IMPDatasetFix.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IMPDatasetFix.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixISE();
        fixIMP();
    }

    private static void fixISE() {
        Database.getInstance().getIseDatasets().findAll().stream()
                .filter(d -> d.getName().contains("NetHEPT"))
                .forEach(d -> {
                    StringBuilder sb = new StringBuilder();
                    Scanner scanner = new Scanner(d.getNetwork());
                    sb.append(scanner.nextLine()).append("\n");
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] splitted = line.split(" ");
                        if (splitted.length < 3) {
                            break;
                        }
                        sb.append(Integer.valueOf(splitted[0]) - 1).append(" ");
                        sb.append(Integer.valueOf(splitted[1]) - 1).append(" ");
                        sb.append(splitted[2]).append("\n");
                    }
                    d.setNetwork(sb.toString());
                    sb = new StringBuilder();
                    scanner = new Scanner(d.getSeeds());
                    while (scanner.hasNextLine()) {
                        try {
                            sb.append(Integer.valueOf(scanner.nextLine()) - 1).append("\n");
                        } catch (NumberFormatException e) {
                            break;
                        }
                    }
                    d.setSeeds(sb.toString());
                    logger.info(Database.getInstance().getIseDatasets().save(d).toString());
                    Database.getInstance().getIseCases().findISECasesByDatasetOrderBySubmitTimeDesc(d)
                            .forEach(c -> {
                                c.setStatus(BaseCase.WAITING);
                                Database.getInstance().getIseCases().save(c);
                            });
                });
    }

    private static void fixIMP() {
        Database.getInstance().getImpDatasets().findAll().stream()
                .filter(d -> d.getName().contains("NetHEPT"))
                .forEach(d -> {
                    StringBuilder sb = new StringBuilder();
                    Scanner scanner = new Scanner(d.getNetwork());
                    sb.append(scanner.nextLine()).append("\n");
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] splitted = line.split(" ");
                        if (splitted.length < 3) {
                            break;
                        }
                        sb.append(Integer.valueOf(splitted[0]) + 1).append(" ");
                        sb.append(Integer.valueOf(splitted[1]) + 1).append(" ");
                        sb.append(splitted[2]).append("\n");
                    }
                    d.setNetwork(sb.toString());
                    logger.info(Database.getInstance().getImpDatasets().save(d).toString());
                    Database.getInstance().getImpCases().findIMPCasesByDatasetOrderBySubmitTimeDesc(d)
                            .forEach(c -> {
                                c.setStatus(BaseCase.WAITING);
                                Database.getInstance().getImpCases().save(c);
                            });
                });
    }

}
