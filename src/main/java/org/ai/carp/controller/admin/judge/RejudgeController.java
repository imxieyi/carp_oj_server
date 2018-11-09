package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.ai.carp.runner.JudgePool;
import org.ai.carp.runner.JudgeRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/judge/rejudge")
public class RejudgeController {

    @GetMapping
    public String get(@RequestParam("cid") String cid, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        if (StringUtils.isEmpty(cid)) {
            throw new InvalidRequestException("No cid!");
        }
        Optional<CARPCase> optCarpCase = Database.getInstance().getCarpCases().findById(cid);
        if (!optCarpCase.isPresent()) {
            throw new InvalidRequestException("Case does not exist!");
        }
        CARPCase carpCase = optCarpCase.get();
        if (carpCase.getStatus() != CARPCase.FINISHED && carpCase.getStatus() != CARPCase.ERROR) {
            throw new InvalidRequestException("Case has not finished!");
        }
        carpCase.setStatus(CARPCase.WAITING);
        carpCase.setStdout("");
        carpCase.setStderr("");
        carpCase.setReason("");
        carpCase.setValid(false);
        carpCase.setCost(0);
        carpCase = Database.getInstance().getCarpCases().save(carpCase);
        JudgeRunner.queue.add(carpCase);
        return "ok";
    }

}