package org.ai.carp.model.judge;

import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface CARPCaseRepository extends MongoRepository<CARPCase, String> {

    List<CARPCase> findCARPCasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countCARPCasesByUser(User user);
    CARPCase findFirstByUserAndValidAndSubmitTimeBeforeOrderBySubmitTimeDesc(User user, boolean valid, Date endTime);
    CARPCase findFirstByUserAndSubmitTimeBeforeOrderBySubmitTimeDesc(User user, Date endTime);
    List<CARPCase> findCARPCasesBySubmitTimeAfter(Date startTime);
    List<CARPCase> findCARPCasesByDatasetOrderBySubmitTimeDesc(CARPDataset dataset);
    List<CARPCase> findCARPCasesByUserOrderBySubmitTimeDesc(User user);
    List<CARPCase> findCARPCasesByUserAndDatasetOrderBySubmitTimeDesc(User user, CARPDataset dataset);
    List<CARPCase> findCARPCasesByStatusNotIn(List<Integer> status);
    List<CARPCase> findCARPCasesByDatasetAndStatusAndValidAndTimedout(CARPDataset dataset, int status, boolean valid, boolean timedout);
    List<CARPCase> findCARPCasesByDatasetAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(CARPDataset dataset, int status, boolean valid);
    List<CARPCase> findCARPCasesByDatasetAndUserAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(CARPDataset dataset, User user, int status, boolean valid, Pageable pageable);
    int countCARPCasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
