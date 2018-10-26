package org.ai.carp.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.DigestUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Document(collection = "users")
public class User implements UserDetails {

    // Types
    public static final int ADMIN = 0;
    public static final int USER = 1;
    public static final int WORKER = 2;

    // Salt
    private static final String salt = "2RmlZNyLBTftD#bq#wFvB1kmyDk*V46V";

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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
