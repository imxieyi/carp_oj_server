package org.ai.carp.model;

import org.ai.carp.model.dataset.CARPDatasetRepository;
import org.ai.carp.model.judge.CARPCaseRepository;
import org.ai.carp.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static Database ourInstance;

    public static Database getInstance() {
        return ourInstance;
    }

    private UserRepository users;
    private CARPDatasetRepository datasets;
    private CARPCaseRepository carpCases;

    public CARPDatasetRepository getDatasets() {
        return datasets;
    }

    @Autowired
    private void setDatasets(CARPDatasetRepository datasets) {
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
}
