package org.ai.carp.controller.login;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.model.Database;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @GetMapping
    public LoginResponse get(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpSession session) {
        String current = (String) session.getAttribute("uid");
        if (current != null) {
            throw new InvalidRequestException("Already logged in!");
        }
        if (StringUtils.isEmpty(username)) {
            throw new InvalidRequestException("No username!");
        }
        if (StringUtils.isEmpty(password)) {
            throw new InvalidRequestException("No password!");
        }
        User user = Database.getInstance().getUsers().findByUsername(username);
        if (user != null && user.passwordMatches(password)) {
            session.setAttribute("uid", user.getId());
            return new LoginResponse(user.getId(), user.getType());
        }
        throw new InvalidRequestException("Wrong username or password!");
    }

}

class LoginResponse {

    private String uid;
    private int type;

    LoginResponse(String uid, int type) {
        this.uid = uid;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public int getType() {
        return type;
    }
}
