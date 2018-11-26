package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class GetOutputController {

    @GetMapping("/judge/details")
    public String get(@RequestParam("cid") String cid, Model model, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No cid!");
        }
        BaseCase baseCase = CaseUtils.findById(cid);
        if (baseCase == null || (user.getType() > User.ADMIN && !user.getId().equals(baseCase.getUserId()))) {
            throw new InvalidRequestException("Case does not exist!");
        }
        if (baseCase.getStatus() != BaseCase.FINISHED && baseCase.getStatus() != BaseCase.ERROR) {
            throw new InvalidRequestException("Case has not finished!");
        }
        CaseUtils.renderView(baseCase, model);
        return "output";
    }

}
