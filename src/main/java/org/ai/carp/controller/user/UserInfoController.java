package org.ai.carp.controller.user;

import org.ai.carp.controller.util.ResponseBase;
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
    public ResponseBase get(HttpSession session) {
        Object opt = UserUtils.getUser(session, User.MAX);
        if (opt instanceof ResponseBase) {
            return (ResponseBase)opt;
        }
        User user = (User)opt;
        return new UserInfoResponse(true, "", user.getId(), user.getUsername(), user.getType());
    }

}

class UserInfoResponse extends ResponseBase {

    private String uid;
    private String username;
    private int type;

    UserInfoResponse(String reason) {
        this(false, reason, "", "", -1);
    }

    UserInfoResponse(boolean ok, String reason, String uid, String username, int type) {
        super(ok, reason);
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
