package org.ai.carp.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.DigestUtils;

import javax.validation.constraints.NotNull;

@Document(collection = "users")
public class User {

    // Types
    public static final int ADMIN = 0;
    public static final int USER = 1;
    public static final int WORKER = 2;

    // Salt
    private static final String salt = "2RmlZNyLBTftD#bq#wFvB1kmyDk*V46V";

    @Id
    public String id;

    public String username;
    public String password;

    public int type;

    public User() {}

    public User(@NotNull String username, @NotNull String password) {
        this(username, password, USER);
    }

    public User(@NotNull String username, @NotNull String password, int type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public static String getHash(String text) {
        return DigestUtils.md5DigestAsHex((text + salt).getBytes());
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s]",
                id, username);
    }
}
