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
@RequestMapping("/api/admin/user/change/password")
public class ChangePasswordController {

    @PostMapping
    public String post(@RequestBody AdminChangePasswordRequest request, HttpSession session) {
        User adminUser = UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(request.username)) {
            throw new InvalidRequestException("No username!");
        }
        if (StringUtils.isEmpty(request.password)) {
            throw new InvalidRequestException("No password!");
        }
        if (request.password.length() > 32) {
            throw new InvalidRequestException("Password too long!");
        }
        int[] validTypes = adminUser.getType() == User.ROOT ? ValidTypes.ROOT : ValidTypes.ADMIN;
        User targetUser = Database.getInstance().getUsers().findByUsername(request.username);
        if (IntStream.of(validTypes).noneMatch(x -> x == targetUser.getType())) {
            throw new PermissionDeniedException(
                    "You are not allowed to change password for user type " + targetUser.getType());
        }
        targetUser.setPassword(request.password);
        Database.getInstance().getUsers().save(targetUser);
        return "done";
    }

}

class AdminChangePasswordRequest {
    public String username;
    public String password;
}
