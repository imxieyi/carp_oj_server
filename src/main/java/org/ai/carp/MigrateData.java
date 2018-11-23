package org.ai.carp;

import org.ai.carp.model.OldDatabase;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.OldDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
public class MigrateData {

    private static final Logger logger = LoggerFactory.getLogger(MigrateData.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CARPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        migrate();
    }

    private static void migrate() {
        // Migrate dataset
        Map<String, CARPDataset> datasetMap = new HashMap<>();
        OldDatabase.getInstance().getOldDatasetRepository().findAll()
                .stream().map(OldDataset::convert)
                .forEach(d -> {
                    datasetMap.put(d.getName(), d);
                    logger.info(d.toString());
                });
        // Migrate case
        OldDatabase.getInstance().getOldCARPCaseRepository().findAll()
                .forEach(c -> {
                    if (c.getDataset() != null) {
                        logger.info(c.convert(datasetMap.get(c.getDataset().getName())).toString());
                    }
                });
        // Delete old data
        OldDatabase.getInstance().getOldCARPCaseRepository().deleteAll();
        OldDatabase.getInstance().getOldDatasetRepository().deleteAll();
    }

}

