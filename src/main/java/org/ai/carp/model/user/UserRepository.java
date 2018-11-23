package org.ai.carp.model.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAllByType(int type);
    User findByUsername(String username);
    void deleteUsersByUsername(String username);

}
