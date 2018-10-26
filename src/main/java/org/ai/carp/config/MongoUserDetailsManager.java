package org.ai.carp.config;

import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class MongoUserDetailsManager implements UserDetailsManager {
    @Override
    public void createUser(UserDetails user) {
        Database.getInstance().getUsers()
                .insert(new User(user.getUsername(), User.getHash(user.getPassword())));
    }

    @Override
    public void updateUser(UserDetails user) {
    }

    @Override
    public void deleteUser(String username) {
        Database.getInstance().getUsers()
                .deleteUsersByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = Database.getInstance().getUsers().findByUsername(auth.getName());
        String oldpass = User.getHash(oldPassword);
        if (oldpass.equals(u.getPassword())) {
            u.setPassword(User.getHash(newPassword));
            Database.getInstance().getUsers().save(u);
        }
    }

    @Override
    public boolean userExists(String username) {
        return (Database.getInstance().getUsers().findByUsername(username) != null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User u = Database.getInstance().getUsers().findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("User " + username + " does not exist!");
        }
        return u;
    }
}
