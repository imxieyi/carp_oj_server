package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
public class CARPSetup {

    private static final Logger logger = LoggerFactory.getLogger(CARPSetup.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CARPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        addUsers();
        addDatasets();
    }

    private static void addUsers() {
        if (Database.getInstance().getUsers().findByUsername("root") == null) {
            Database.getInstance().getUsers().insert(new User("root", "123", User.ROOT));
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("users.csv");
        if (is == null) {
            logger.error("users.csv not found!");
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
            if (Database.getInstance().getUsers().findByUsername(splitted[0]) == null) {
                int role;
                switch (splitted[1]) {
                    case "ADMIN":
                        role = User.ADMIN;
                        break;
                    case "USER":
                        role = User.USER;
                        break;
                    case "WORKER":
                        role = User.WORKER;
                        break;
                    default:
                        logger.error("Invalid user info for {}", splitted[0]);
                        continue;
                }
                User user = new User(splitted[0], splitted[2], role);
                user = Database.getInstance().getUsers().insert(user);
                logger.info(user.toString());
            }
        }
        scanner.close();
    }

    private static void addDatasets() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_carp.csv");
        if (is == null) {
            logger.error("datasets_carp.csv not found");
            return;
        }
        Scanner scanner = new Scanner(is);
        Map<String, CARPDataset> map = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\r", "");
            String[] splitted = line.split(",");
            if (StringUtils.isEmpty(splitted[0])) {
                continue;
            }
            map.put(splitted[0], new CARPDataset(splitted[0], Integer.valueOf(splitted[1])
                    , Integer.valueOf(splitted[2]), Integer.valueOf(splitted[3]), ""));
        }
        try {
            File datasets = new File(classLoader.getResource("datasets_carp").toURI());
            File[] list = datasets.listFiles((dir, name) -> name.endsWith(".dat"));
            for (File f : list) {
                try {
                    String name = f.getName().replaceAll(".dat", "");
                    if (Database.getInstance().getCarpDatasets().findDatasetByName(name) != null) {
                        continue;
                    }
                    String content = new Scanner(f).useDelimiter("\\Z").next().replace("\r", "");
                    CARPDataset dataset = map.get(name);
                    if (dataset == null) {
                        logger.error("Definition not found for {}", name);
                        continue;
                    }
                    dataset.setData(content);
                    dataset = Database.getInstance().getCarpDatasets().insert(dataset);
                    logger.info(dataset.toString());
                } catch (FileNotFoundException e) {
                    logger.error("Failed to read dataset {}", f.getName(), e);
                }
            }
        } catch (URISyntaxException e) {
            logger.error("Failed to get dataset path!", e);
        }
    }

}
