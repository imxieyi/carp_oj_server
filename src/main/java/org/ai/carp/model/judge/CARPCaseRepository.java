package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CARPCaseRepository extends MongoRepository<CARPCase, String> {

    CARPCase findCARPCaseById(String id);
    List<CARPCase> findCARPCasesByUserOrderBySubmitTimeDesc(User user);
    List<CARPCase> findCARPCasesByDatasetOrderBySubmitTimeDesc(Dataset dataset);
    List<CARPCase> findCARPCasesByDatasetAndStatusAndValidOrderByCostAsc(Dataset dataset, int status, boolean valid);

}
