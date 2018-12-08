package org.ai.carp;

import org.ai.carp.controller.util.ISEUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.ISEDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Scanner;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class ISEFixBias {

    private static final Logger logger = LoggerFactory.getLogger(ISEFixBias.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ISEFixBias.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        fixDataset();
        fixCase();
    }

    private static void fixDataset() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_ise.csv");
        if (is == null) {
            logger.error("datasets_ise.csv not found");
            return;
        }
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\r", "");
            String[] splitted = line.split(",");
            if (StringUtils.isEmpty(splitted[0])) {
                continue;
            }
            String name = splitted[0] + "-" + splitted[1] + "-" + splitted[2];
            ISEDataset dataset = Database.getInstance().getIseDatasets().findDatasetByName(name);
            if (dataset == null) {
                logger.error("Dataset not found: {}", name);
                continue;
            }
            dataset.setInfluence(Double.parseDouble(splitted[6]));
            dataset.setBias(Double.parseDouble(splitted[7]));
            logger.info(Database.getInstance().getIseDatasets().save(dataset).toString());
        }
    }

    private static void fixCase() {
        Database.getInstance().getIseCases().findAll().forEach(c -> {
            c.setValid(false);
            c.setReason(null);
            c.setResult(0);
            ISEUtils.checkResult(c);
            logger.info(Database.getInstance().getIseCases().save(c).toString());
        });
    }

}
