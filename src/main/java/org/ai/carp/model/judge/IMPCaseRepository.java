package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface IMPCaseRepository extends MongoRepository<IMPCase, String> {

    List<IMPCase> findIMPCasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countIMPCasesByUser(User user);
    List<IMPCase> findIMPCasesByDatasetOrderBySubmitTimeDesc(IMPDataset dataset);
    List<IMPCase> findIMPCasesByUserOrderBySubmitTimeDesc(User user);
    List<IMPCase> findIMPCasesByUserAndDatasetOrderBySubmitTimeDesc(User user, IMPDataset dataset);
    List<IMPCase> findIMPCasesByStatusNotIn(List<Integer> status);
    List<IMPCase> findIMPCasesByDatasetAndStatusAndValidAndTimedout(IMPDataset dataset, int status, boolean valid, boolean timedout);
    List<IMPCase> findIMPCasesByDatasetAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(IMPDataset dataset, int status, boolean valid);
    List<IMPCase> findIMPCasesByDatasetAndUserAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(IMPDataset dataset, User user, int status, boolean valid, Pageable pageable);
    int countIMPCasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
