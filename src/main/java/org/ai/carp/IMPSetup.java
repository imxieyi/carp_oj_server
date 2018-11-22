package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
public class IMPSetup {

    private static final Logger logger = LoggerFactory.getLogger(IMPSetup.class);

    public static void main(String[] args) throws URISyntaxException {
        SpringApplication app = new SpringApplication(IMPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        addISEDatasets();
        addIMPDatasets();
    }

    private static void addISEDatasets() throws URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_ise.csv");
        if (is == null) {
            logger.error("datasets_ise.csv not found");
            return;
        }
        Scanner scanner = new Scanner(is);
        File datasets = new File(classLoader.getResource("datasets_imp").toURI());
        File[] list = datasets.listFiles((dir, name) -> name.endsWith(".txt"));
        Map<String, File> fileMap = new HashMap<>();
        for (File f : list) {
            fileMap.put(f.getName().replace(".txt", ""), f);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\r", "");
            String[] splitted = line.split(",");
            if (StringUtils.isEmpty(splitted[0])) {
                continue;
            }
            String name = splitted[0] + "-" + splitted[1];
            if (Database.getInstance().getIseDatasets().findDatasetByName(name) != null) {
                continue;
            }
            try {
                File networkFile = fileMap.get(splitted[0]);
                String network = new Scanner(networkFile).useDelimiter("\\Z").next().replace("\r", "");
                File seedsFile = fileMap.get(splitted[1]);
                String seeds = new Scanner(seedsFile).useDelimiter("\\Z").next().replace("\r", "");
                ISEDataset dataset = new ISEDataset(name, Integer.valueOf(splitted[2])
                        , Integer.valueOf(splitted[3]), Integer.valueOf(splitted[4]), network, seeds);
                dataset = Database.getInstance().getIseDatasets().insert(dataset);
                logger.info(dataset.toString());
            } catch (Exception e) {
                logger.error("Error adding dataset", e);
            }
        }
    }

    private static void addIMPDatasets() throws URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_imp.csv");
        if (is == null) {
            logger.error("datasets_imp.csv not found");
            return;
        }
        Scanner scanner = new Scanner(is);
        File datasets = new File(classLoader.getResource("datasets_imp").toURI());
        File[] list = datasets.listFiles((dir, name) -> name.endsWith(".txt"));
        Map<String, File> fileMap = new HashMap<>();
        for (File f : list) {
            fileMap.put(f.getName().replace(".txt", ""), f);
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\r", "");
            String[] splitted = line.split(",");
            if (StringUtils.isEmpty(splitted[0])) {
                continue;
            }
            String name = splitted[0];
            if (Database.getInstance().getImpDatasets().findDatasetByName(name) != null) {
                continue;
            }
            try {
                File networkFile = fileMap.get(splitted[0]);
                String network = new Scanner(networkFile).useDelimiter("\\Z").next().replace("\r", "");
                IMPDataset dataset = new IMPDataset(name, Integer.valueOf(splitted[1])
                        , Integer.valueOf(splitted[2]), Integer.valueOf(splitted[3]), network);
                dataset = Database.getInstance().getImpDatasets().insert(dataset);
                logger.info(dataset.toString());
            } catch (Exception e) {
                logger.error("Error adding dataset", e);
            }
        }
    }

}
