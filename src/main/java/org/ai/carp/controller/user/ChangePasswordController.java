package org.ai.carp.controller.user;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user/change/password")
public class ChangePasswordController {

    @GetMapping
    public ChangeInfoResponse get(@RequestParam("old") String oldP,
                            @RequestParam("new") String newP,
                            HttpSession session) {
        if (StringUtils.isEmpty(oldP)) {
            throw new InvalidRequestException("No old password!");
        }
        if (StringUtils.isEmpty(newP)) {
            throw new InvalidRequestException("No new password!");
        }
        if (newP.length() > 32) {
            throw new InvalidRequestException("Password too long!");
        }
        User user = UserUtils.getUser(session, User.MAX);
        if (!user.passwordMatches(oldP)) {
            throw new InvalidRequestException("Wrong old password!");
        }
        user.setPassword(newP);
        Database.getInstance().getUsers().save(user);
        return new ChangeInfoResponse();
    }

}
