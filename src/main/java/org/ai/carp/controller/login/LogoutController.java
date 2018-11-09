package org.ai.carp.controller.login;

import org.ai.carp.controller.exceptions.NotLoggedInException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    @GetMapping
    public LogoutResponse get(HttpSession session) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            throw new NotLoggedInException("Not logged in!");
        }
        session.invalidate();
        return new LogoutResponse(uid);
    }

}

class LogoutResponse {

    private String uid;

    LogoutResponse(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
