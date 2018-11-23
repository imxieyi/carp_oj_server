package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface IMPCaseRepository extends MongoRepository<IMPCase, String> {

    List<IMPCase> findIMPCasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countIMPCasesByUser(User user);
    List<IMPCase> findIMPCasesByDatasetOrderBySubmitTimeDesc(CARPDataset dataset);
    List<IMPCase> findIMPCasesByUserAndDatasetOrderBySubmitTimeDesc(User user, CARPDataset dataset);
    List<IMPCase> findIMPCasesByStatusNotIn(List<Integer> status);
    List<IMPCase> findIMPCasesByDatasetAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(CARPDataset dataset, int status, boolean valid);
    List<IMPCase> findIMPCasesByDatasetAndUserAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(CARPDataset dataset, User user, int status, boolean valid, Pageable pageable);
    int countIMPCasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
