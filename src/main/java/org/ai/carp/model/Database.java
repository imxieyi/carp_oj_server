package org.ai.carp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.dataset.DatasetRepository;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.CARPCaseRepository;
import org.ai.carp.model.user.User;
import org.ai.carp.model.user.UserRepository;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static Database ourInstance;

    public static Database getInstance() {
        return ourInstance;
    }

    private UserRepository users;
    private DatasetRepository datasets;
    private CARPCaseRepository carpCases;

    public DatasetRepository getDatasets() {
        return datasets;
    }

    @Autowired
    private void setDatasets(DatasetRepository datasets) {
        this.datasets = datasets;
    }

    public CARPCaseRepository getCarpCases() {
        return carpCases;
    }

    @Autowired
    private void setCarpCases(CARPCaseRepository carpCases) {
        this.carpCases = carpCases;
    }

    public UserRepository getUsers() {
        return users;
    }

    @Autowired
    public void setUsers(UserRepository users) {
        this.users = users;
    }

    private Database() {
        ourInstance = this;
    }

    @PostConstruct
    public void testDB() {
        if (users.count() == 0) {
            users.insert(new User("test", "123"));
            users.insert(new User("root", "123", User.ROOT));
            users.insert(new User("judge", "123", User.WORKER));
        }
        User user = getUsers().findByUsername("test");
        User nu = getUsers().findByUsername("gg");
        if (nu != null) {
            throw new RuntimeException("gg");
        }
        logger.info(user.toString());
        user.setPassword("234");
        getUsers().save(user);
        if (datasets.count() == 0) {
            datasets.insert(new Dataset("test", 30, 1024, 8, ""));
        }
        Dataset dataset = getDatasets().findDatasetByName("test");
        logger.info(dataset.toString());
        if (carpCases.count() == 0) {
            carpCases.insert(new CARPCase(user, dataset, new Binary("test".getBytes())));
        }
        List<CARPCase> cases = getCarpCases().findCARPCasesByUserOrderBySubmitTimeDesc(user);
        for (CARPCase c : cases) {
            logger.info(c.toString());
        }
        cases = getCarpCases().findCARPCasesByDatasetOrderBySubmitTimeDesc(dataset);
        ObjectMapper mapper = new ObjectMapper();
        for (CARPCase c : cases) {
            logger.info(c.toString());
            try {
                logger.info(mapper.writeValueAsString(c));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
