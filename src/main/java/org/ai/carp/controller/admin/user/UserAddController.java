package org.ai.carp.controller.admin.user;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/admin/user/add")
public class UserAddController {

    @PostMapping
    public UserAddResponse post(@RequestBody PostUser user, HttpSession session) {
        User logged = UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(user.username)) {
            throw new InvalidRequestException("No username!");
        }
        if (StringUtils.isEmpty(user.password)) {
            throw new InvalidRequestException("No password!");
        }
        if (user.username.length() > 32) {
            throw new InvalidRequestException("Username too long!");
        }
        if (user.password.length() > 32) {
            throw new InvalidRequestException("Password too long!");
        }
        int[] validTypes;
        if (logged.getType() == User.ROOT) {
            validTypes = ValidTypes.ROOT;
        } else if (logged.getType() == User.ADMIN) {
            validTypes = ValidTypes.ADMIN;
        } else {
            throw new PermissionDeniedException("You are not allowed to create user!");
        }
        if (IntStream.of(validTypes).noneMatch(x -> x == user.type)) {
            throw new PermissionDeniedException(
                    "You are not allowed to create user of type " + user.type + "!");
        }
        if (Database.getInstance().getUsers().findByUsername(user.username) != null) {
            throw new InvalidRequestException("User already exists!");
        }
        User inserted = Database.getInstance().getUsers().insert(
                new User(user.username, user.password, user.type)
        );
        return new UserAddResponse(inserted.getId());
    }

}

class UserAddResponse {

    private String id;

    public UserAddResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class PostUser {
    public String username;
    public String password;
    public int type;
}
