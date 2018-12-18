package org.ai.carp;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.judge.IMPCase;
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
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"org.ai.carp.model"})
@SpringBootApplication
@EnableMongoRepositories("org.ai.carp.model")
public class IMPJudgeFinal {

    private static final Logger logger = LoggerFactory.getLogger(IMPJudgeFinal.class);

    public static void main(String[] args) throws FileNotFoundException {
        SpringApplication app = new SpringApplication(IMPJudgeFinal.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        disableDatasets();
        addDatasets();
        addCases();
    }

    private static void disableDatasets() {
        Database.getInstance().getImpDatasets().findAll().forEach(c -> {
            c.setEnabled(false);
            Database.getInstance().getImpDatasets().save(c);
            logger.info(c.toString());
        });
    }

    private static void addDatasets() throws FileNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("datasets_imp_final.csv");
        if (is == null) {
            logger.error("datasets_imp_final.csv not found");
            return;
        }
        Scanner scanner = new Scanner(is);
        String datasetPath = classLoader.getResource("datasets_imp_final").getPath();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\r", "");
            String[] splitted = line.split(",");
            if (StringUtils.isEmpty(splitted[0])) {
                continue;
            }
            String network = new Scanner(new File(datasetPath + "/" + splitted[0] + ".txt"))
                    .useDelimiter("\\Z").next().replace("\r", "");
            String name = String.format("%s-%s-%s", splitted[0], splitted[1], splitted[2]);
            IMPDataset dataset = new IMPDataset(name, Integer.valueOf(splitted[3])
                    , Integer.valueOf(splitted[4]), Integer.valueOf(splitted[5])
                    , Integer.valueOf(splitted[1]), splitted[2], network);
            dataset.setEnabled(true);
            dataset.setSubmittable(false);
            dataset.setFinalJudge(true);
            dataset = Database.getInstance().getImpDatasets().insert(dataset);
            logger.info(dataset.toString());
        }
    }

    private static void addCases() {
        Date endTime = new Date(1544803200000L);
        // Query datasets
        List<IMPDataset> datasets = Database.getInstance().getImpDatasets().findAll()
                .stream().filter(BaseDataset::isFinalJudge).collect(Collectors.toList());
        // Query users
        List<User> users = Database.getInstance().getUsers().findAllByType(User.USER);
        List<IMPCase> cases = new ArrayList<>();
        for (User u : users) {
            IMPCase submission = Database.getInstance().getImpCases()
                    .findFirstByUserAndSubmitTimeBeforeOrderBySubmitTimeDesc(u, endTime);
            if (submission == null || submission.getArchive() == null) {
                continue;
            }
            for (IMPDataset dataset : datasets) {
                for (int i=0; i<5; i++) {
                    cases.add(new IMPCase(u, dataset, submission.getArchive()));
                }
            }
        }
        Collections.shuffle(cases);
        for (IMPCase c : cases) {
            IMPCase newC = Database.getInstance().getImpCases().insert(c);
            Database.getInstance().getLiteCases().insert(new LiteCase(c));
            logger.info(newC.toString());
        }
    }

}
