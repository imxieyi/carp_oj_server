package org.ai.carp.controller.user;

import org.ai.carp.controller.util.ResponseBase;
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
    public ResponseBase get(@RequestParam("old") String oldP,
                            @RequestParam("new") String newP,
                            HttpSession session) {
        if (StringUtils.isEmpty(oldP)) {
            return new ChangeInfoResponse("No old password!");
        }
        if (StringUtils.isEmpty(newP)) {
            return new ChangeInfoResponse("No new password!");
        }
        if (newP.length() > 32) {
            return new ChangeInfoResponse("Password too long!");
        }
        Object opt = UserUtils.getUser(session, User.MAX);
        if (opt instanceof ResponseBase) {
            return (ResponseBase) opt;
        }
        User user = (User) opt;
        if (!user.passwordMatches(oldP)) {
            return new ChangeInfoResponse("Wrong old password!");
        }
        user.setPassword(newP);
        Database.getInstance().getUsers().save(user);
        return new ChangeInfoResponse(true, null);
    }

}
