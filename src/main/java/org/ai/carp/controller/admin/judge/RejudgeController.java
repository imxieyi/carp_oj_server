package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.LiteCase;
import org.ai.carp.model.user.User;
import org.ai.carp.runner.JudgeRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/judge/rejudge")
public class RejudgeController {

    @PostMapping
    public String post(@RequestBody RejudgeRequest rejudge, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(rejudge.cid)) {
            throw new InvalidRequestException("No cid!");
        }
        LiteCase optLiteCase = Database.getInstance().getLiteCases().findLiteCaseByFullId(rejudge.cid);
        if (optLiteCase == null) {
            throw new InvalidRequestException("Case does not exist!");
        }
        BaseCase baseCase = optLiteCase.getFullCase();
        if (baseCase.getStatus() != CARPCase.FINISHED && baseCase.getStatus() != CARPCase.ERROR) {
            throw new InvalidRequestException("Case has not finished!");
        }
        baseCase.setStatus(CARPCase.WAITING);
        baseCase.setStdout("");
        baseCase.setStderr("");
        baseCase.setReason("");
        baseCase.setValid(false);
        baseCase = CaseUtils.saveCase(baseCase);
        JudgeRunner.queue.add(baseCase);
        return "ok";
    }

}

class RejudgeRequest {
    public String cid;
}
