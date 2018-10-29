package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/judge/query")
public class QueryByIdController {

    @GetMapping
    public CARPCase get(@RequestParam("cid") String cid, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No case id!");
        }
        Optional<CARPCase> carpCase = Database.getInstance().getCarpCases().findById(cid);
        if (!carpCase.isPresent()) {
            throw new InvalidRequestException("Invalid case id!");
        }
        return carpCase.get();
    }

}
