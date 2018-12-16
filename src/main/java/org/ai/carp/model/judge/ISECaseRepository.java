package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.ISEDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ISECaseRepository extends MongoRepository<ISECase, String> {

    List<ISECase> findISECasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countCARPCasesByUser(User user);
    ISECase findFirstByUserAndSubmitTimeBeforeOrderBySubmitTimeDesc(User user, Date endTime);
    List<ISECase> findISECasesByDatasetOrderBySubmitTimeDesc(ISEDataset dataset);
    List<ISECase> findISECasesByUserOrderBySubmitTimeDesc(User user);
    List<ISECase> findISECasesByUserAndDatasetOrderBySubmitTimeDesc(User user, ISEDataset dataset);
    List<ISECase> findISECasesByStatusNotIn(List<Integer> status);
    List<ISECase> findISECasesByDatasetAndStatusAndValidAndTimedout(ISEDataset dataset, int status, boolean valid, boolean timedout);
    List<ISECase> findISECasesByDatasetAndStatusAndValidOrderByTimeAscSubmitTimeAsc(ISEDataset dataset, int status, boolean valid);
    List<ISECase> findISECasesByDatasetAndUserAndStatusAndValidOrderByTimeAscSubmitTimeAsc(ISEDataset dataset, User user, int status, boolean valid, Pageable pageable);
    int countISECasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
