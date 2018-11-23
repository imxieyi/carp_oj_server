package org.ai.carp.model;

import org.ai.carp.model.dataset.OldDatasetRepository;
import org.ai.carp.model.judge.OldCARPCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OldDatabase {

    private static OldDatabase ourInstance;

    public static OldDatabase getInstance() {
        return ourInstance;
    }

    private OldDatasetRepository oldDatasetRepository;
    private OldCARPCaseRepository oldCARPCaseRepository;

    @Autowired
    private void setOldDatasetRepository(OldDatasetRepository oldDatasetRepository) {
        this.oldDatasetRepository = oldDatasetRepository;
    }

    @Autowired
    private void setOldCARPCaseRepository(OldCARPCaseRepository oldCARPCaseRepository) {
        this.oldCARPCaseRepository = oldCARPCaseRepository;
    }

    public OldCARPCaseRepository getOldCARPCaseRepository() {
        return oldCARPCaseRepository;
    }

    public OldDatasetRepository getOldDatasetRepository() {
        return oldDatasetRepository;
    }

    private OldDatabase() {
        ourInstance = this;
    }
}
