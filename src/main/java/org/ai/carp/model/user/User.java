package org.ai.carp.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;

@Document(collection = "users")
public class User {

    // Types
    public static final int NONE = -100;
    public static final int ROOT = 0;
    public static final int ADMIN = 100;
    public static final int USER = 200;
    public static final int WORKER = 300;
    public static final int MAX = 1000; // Never use

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    private int type;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public boolean passwordMatches(String pass) {
        return passwordEncoder.matches(pass, password);
    }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User() {}

    public User(@NotNull String username, @NotNull String password) {
        this(username, password, USER);
    }

    public User(@NotNull String username, @NotNull String password, int type) {
        this.username = username;
        this.password = passwordEncoder.encode(password);
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s]",
                id, username);
    }
}
