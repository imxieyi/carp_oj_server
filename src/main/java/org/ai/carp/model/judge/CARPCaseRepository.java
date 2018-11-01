package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface CARPCaseRepository extends MongoRepository<CARPCase, String> {

    List<CARPCase> findCARPCasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countCARPCasesByUser(User user);
    List<CARPCase> findCARPCasesByDatasetOrderBySubmitTimeDesc(Dataset dataset);
    List<CARPCase> findCARPCasesByStatusIsNot(int status);
    List<CARPCase> findCARPCasesByDatasetAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(Dataset dataset, int status, boolean valid);
    int countCARPCasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
