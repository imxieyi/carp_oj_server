package org.ai.carp.controller.user;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    @GetMapping
    public UserInfoResponse get(HttpSession session) {
        User user = UserUtils.getUser(session, User.MAX);
        return new UserInfoResponse(user.getId(), user.getUsername(), user.getType());
    }

}

class UserInfoResponse {

    private String uid;
    private String username;
    private int type;

    UserInfoResponse(String uid, String username, int type) {
        this.uid = uid;
        this.username = username;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public int getType() {
        return type;
    }
}
