package org.ai.carp.controller.login;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    @GetMapping
    public LoginResponse get(HttpSession session) {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            throw new InvalidRequestException("Not logged in!");
        }
        session.invalidate();
        return new LoginResponse(uid);
    }

}
