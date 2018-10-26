package org.ai.carp.model;

import org.ai.carp.model.user.User;
import org.ai.carp.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private static Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    public UserRepository getUsers() {
        return users;
    }

    @Autowired
    public void setUsers(UserRepository users) {
        this.users = users;
    }

    private UserRepository users;

    private Database() {
    }

    @PostConstruct
    public void testDB() {
        if (users.count() == 0) {
            // Insert test account
            users.insert(new User("test", User.getHash("123")));
        }
        User user = getUsers().findByUsername("test");
        logger.info(user.toString());
    }
}
