package org.ai.carp.model;

import org.ai.carp.model.dataset.CARPDatasetRepository;
import org.ai.carp.model.dataset.IMPDatasetRepository;
import org.ai.carp.model.dataset.ISEDatasetRepository;
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
    private CARPDatasetRepository carpDatasets;
    private ISEDatasetRepository iseDatasets;
    private IMPDatasetRepository impDatasets;
    private CARPCaseRepository carpCases;

    public CARPDatasetRepository getCarpDatasets() {
        return carpDatasets;
    }

    @Autowired
    private void setCARPDatasets(CARPDatasetRepository datasets) {
        this.carpDatasets = datasets;
    }

    public ISEDatasetRepository getIseDatasets() {
        return iseDatasets;
    }

    @Autowired
    public void setIseDatasets(ISEDatasetRepository iseDatasets) {
        this.iseDatasets = iseDatasets;
    }

    public IMPDatasetRepository getImpDatasets() {
        return impDatasets;
    }

    @Autowired
    public void setImpDatasets(IMPDatasetRepository impDatasets) {
        this.impDatasets = impDatasets;
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
