package org.ai.carp.controller.judge;

import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.judge.LiteCase;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/judge/get")
public class QueryAllController {

    @GetMapping
    public QueryResult get(@RequestParam("page") int page,
                           @RequestParam("size") int size,
                           HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        List<LiteCase> liteCases = Database.getInstance().getLiteCases()
                .findLiteCasesByUserOrderBySubmitTimeDesc(user, PageRequest.of(page, size));
        List<BaseCase> baseCases = liteCases.stream()
                .map(LiteCase::getFullCase)
                .collect(Collectors.toList());
        int total = Database.getInstance().getLiteCases().countLiteCasesByUser(user);
        return new QueryResult(baseCases, total);
    }

}

