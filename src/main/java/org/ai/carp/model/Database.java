package org.ai.carp.model;

import org.ai.carp.model.dataset.CARPDatasetRepository;
import org.ai.carp.model.dataset.IMPDatasetRepository;
import org.ai.carp.model.dataset.ISEDatasetRepository;
import org.ai.carp.model.judge.CARPCaseRepository;
import org.ai.carp.model.judge.IMPCaseRepository;
import org.ai.carp.model.judge.ISECaseRepository;
import org.ai.carp.model.judge.LiteCaseRepository;
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
    private ISECaseRepository iseCases;
    private IMPCaseRepository impCases;
    private LiteCaseRepository liteCases;

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

    public ISECaseRepository getIseCases() {
        return iseCases;
    }

    @Autowired
    public void setIseCases(ISECaseRepository iseCases) {
        this.iseCases = iseCases;
    }

    public IMPCaseRepository getImpCases() {
        return impCases;
    }

    @Autowired
    public void setImpCases(IMPCaseRepository impCases) {
        this.impCases = impCases;
    }

    public LiteCaseRepository getLiteCases() {
        return liteCases;
    }

    @Autowired
    public void setLiteCases(LiteCaseRepository liteCases) {
        this.liteCases = liteCases;
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
