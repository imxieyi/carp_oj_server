package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ISECaseRepository extends MongoRepository<ISECase, String> {

    List<ISECase> findISECasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countCARPCasesByUser(User user);
    List<ISECase> findISECasesByDatasetOrderBySubmitTimeDesc(CARPDataset dataset);
    List<ISECase> findISECasesByUserAndDatasetOrderBySubmitTimeDesc(User user, CARPDataset dataset);
    List<ISECase> findISECasesByStatusNotIn(List<Integer> status);
    List<ISECase> findISECasesByDatasetAndStatusAndValidOrderByTimeAscSubmitTimeAsc(CARPDataset dataset, int status, boolean valid);
    List<ISECase> findISECasesByDatasetAndUserAndStatusAndValidOrderByTimeAscSubmitTimeAsc(CARPDataset dataset, User user, int status, boolean valid, Pageable pageable);
    int countISECasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
