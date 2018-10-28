package org.ai.carp.controller.login;

import org.ai.carp.controller.util.ResponseBase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    @GetMapping
    public ResponseBase get(HttpSession session) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            return new LoginResponse("Not logged in!");
        }
        session.invalidate();
        return new LoginResponse(true, "", uid);
    }

}
