package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.LiteCase;
import org.ai.carp.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class CARPJudgeFinal {

    private static final Logger logger = LoggerFactory.getLogger(CARPJudgeFinal.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CARPSetup.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        addDatasets();
        addCases();
    }

    private static void addDatasets() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_carp_final.csv");
        if (is == null) {
            logger.error("datasets_carp_final.csv not found");
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
            File datasets = new File(classLoader.getResource("datasets_carp_final").toURI());
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
                    dataset.setEnabled(true);
                    dataset.setSubmittable(false);
                    dataset.setFinalJudge(true);
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

    private static void addCases() {
        Date endTime = new Date(1542964624000L);
        // Query datasets
        List<CARPDataset> datasets = Database.getInstance().getCarpDatasets().findAll()
                .stream().filter(CARPDataset::isFinalJudge).collect(Collectors.toList());
        // Query users
        List<User> users = Database.getInstance().getUsers().findAllByType(User.USER);
        List<CARPCase> cases = new ArrayList<>();
        for (User u : users) {
            CARPCase submission = Database.getInstance().getCarpCases()
                    .findFirstByUserAndSubmitTimeBeforeOrderBySubmitTimeDesc(u, endTime);
            if (submission == null || submission.getArchive() == null) {
                continue;
            }
            for (CARPDataset dataset : datasets) {
                for (int i=0; i<5; i++) {
                    cases.add(new CARPCase(u, dataset, submission.getArchive()));
                }
            }
        }
        Collections.shuffle(cases);
        for (CARPCase c : cases) {
            CARPCase newC = Database.getInstance().getCarpCases().insert(c);
            Database.getInstance().getLiteCases().insert(new LiteCase(c));
            logger.info(newC.toString());
        }
    }

}
