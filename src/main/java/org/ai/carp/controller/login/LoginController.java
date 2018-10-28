package org.ai.carp.controller.login;

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
    public LoginResponse get(@RequestParam("user") String username,
                             @RequestParam("pass") String password,
                             HttpSession session) {
        String current = (String) session.getAttribute("uid");
        if (current != null) {
            return new LoginResponse(true, "Already logged in!", current);
        }
        if (StringUtils.isEmpty(username)) {
            return new LoginResponse("No username!");
        }
        if (StringUtils.isEmpty(password)) {
            return new LoginResponse("No password!");
        }
        User user = Database.getInstance().getUsers().findByUsername(username);
        if (user != null && user.passwordMatches(password)) {
            session.setAttribute("uid", user.getId());
            return new LoginResponse(true, "", user.getId());
        }
        return new LoginResponse("Wrong username or password!");
    }

}

