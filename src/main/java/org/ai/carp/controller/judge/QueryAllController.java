package org.ai.carp.controller.judge;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/judge/all")
public class QueryAllController {

    @GetMapping
    public QueryResult get(HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        List<CARPCase> carpCases = Database.getInstance().getCarpCases()
                .findCARPCasesByUserOrderBySubmitTimeDesc(user);
        return new QueryResult(carpCases);
    }

}

