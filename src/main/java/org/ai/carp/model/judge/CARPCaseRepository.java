package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CARPCaseRepository extends MongoRepository<CARPCase, String> {

    CARPCase findCARPCaseById(String id);
    List<CARPCase> findCARPCasesByUser(User user);
    List<CARPCase> findCARPCasesByDataset(Dataset dataset);
    List<CARPCase> findCARPCasesByDatasetOrderByCost(Dataset dataset);

}
