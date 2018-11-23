package org.ai.carp.model.judge;

import org.ai.carp.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface LiteCaseRepository extends MongoRepository<LiteCase, String> {

    List<LiteCase> findLiteCasesByUserOrderBySubmitTimeDesc(User user, Pageable pageable);
    int countLiteCasesByUser(User user);
    int countLiteCasesByUserAndSubmitTimeAfter(User user, Date startTime);

}
