package org.ai.carp.controller.user;

import org.ai.carp.controller.ResponseBase;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    @GetMapping
    public UserInfoResponse get(HttpSession session) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            return new UserInfoResponse("Not logged in!");
        }
        Optional<User> optionalUser = Database.getInstance().getUsers().findById(uid);
        if (!optionalUser.isPresent()) {
            // Session should be destroyed
            session.invalidate();
            return new UserInfoResponse("User does not exist!");
        }
        User user = optionalUser.get();
        return new UserInfoResponse(true, "", uid, user.getUsername(), user.getType());
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
