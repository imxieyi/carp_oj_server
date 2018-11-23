package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/judge/query")
public class QueryByIdController {

    @GetMapping
    public BaseCase get(@RequestParam("cid") String cid, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No case id!");
        }
        BaseCase baseCase = CaseUtils.findById(cid);
        if (baseCase == null) {
            throw new InvalidRequestException("Invalid case id!");
        }
        if (!baseCase.getUserId().equals(user.getId())) {
            throw new PermissionDeniedException("You don't own this case!");
        }
        return baseCase;
    }

}
