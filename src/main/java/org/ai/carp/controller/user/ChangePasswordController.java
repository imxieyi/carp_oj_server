package org.ai.carp.controller.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user/change/password")
public class ChangePasswordController {

    @PostMapping
    public ChangePasswordResponse get(@RequestBody ChangePasswordRequest request, HttpSession session) {
        if (StringUtils.isEmpty(request.oldP)) {
            throw new InvalidRequestException("No old password!");
        }
        if (StringUtils.isEmpty(request.newP)) {
            throw new InvalidRequestException("No new password!");
        }
        if (request.newP.length() > 32) {
            throw new InvalidRequestException("Password too long!");
        }
        User user = UserUtils.getUser(session, User.MAX);
        if (!user.passwordMatches(request.oldP)) {
            throw new InvalidRequestException("Wrong old password!");
        }
        user.setPassword(request.newP);
        Database.getInstance().getUsers().save(user);
        return new ChangePasswordResponse(user.getId());
    }

}

class ChangePasswordRequest {
    @JsonProperty("old")
    public String oldP;
    @JsonProperty("new")
    public String newP;
}

class ChangePasswordResponse {

    private String uid;

    ChangePasswordResponse(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
