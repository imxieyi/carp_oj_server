package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.controller.util.ArchiveUtils;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.ai.carp.runner.JudgeRunner;
import org.bson.types.Binary;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/judge/submit")
public class SubmitController {

    @PostMapping
    public SubmitResponse post(@RequestBody PostCase postCase, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (new Date().getTime() >= 1542816000000L) {
            throw new InvalidRequestException("Deadline has passed!");
        }
        if (StringUtils.isEmpty(postCase.dataset)) {
            throw new InvalidRequestException("No dataset!");
        }
        if (StringUtils.isEmpty(postCase.data)) {
            throw new InvalidRequestException("No data!");
        }
        Optional<CARPDataset> dataset = Database.getInstance().getCarpDatasets().findById(postCase.dataset);
        if (!dataset.isPresent()) {
            throw new InvalidRequestException("Invalid dataset!");
        }
        if (user.getType() == User.USER &&
                CaseUtils.countPreviousDay(user) >= CARPCase.DAILY_LIMIT) {
            throw new PermissionDeniedException("You have reached daily limits on submission!");
        }
        Binary archive = ArchiveUtils.convertSubmission(postCase.data);
        CARPCase carpCase = Database.getInstance().getCarpCases().insert(new CARPCase(user, dataset.get(), archive));
        JudgeRunner.queue.add(carpCase);
        int remain = CARPCase.DAILY_LIMIT - CaseUtils.countPreviousDay(user);
        return new SubmitResponse(carpCase.getId(), remain);
    }

}

class SubmitResponse {

    private String cid;
    private int remain;

    public SubmitResponse(String cid, int remain) {
        this.cid = cid;
        this.remain = remain;
    }

    public String getCid() {
        return cid;
    }

    public int getRemain() {
        return remain;
    }
}

class PostCase {
    public String dataset;
    public String data;
}
