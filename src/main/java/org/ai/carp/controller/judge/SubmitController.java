package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.exceptions.PermissionDeniedException;
import org.ai.carp.controller.util.ArchiveUtils;
import org.ai.carp.controller.util.CaseUtils;
import org.ai.carp.controller.util.DatasetUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;
import org.ai.carp.model.judge.*;
import org.ai.carp.model.user.User;
import org.ai.carp.runner.JudgeRunner;
import org.bson.types.Binary;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/judge/submit")
public class SubmitController {

    @PostMapping
    public SubmitResponse post(@RequestBody PostCase postCase, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(postCase.data)) {
            throw new InvalidRequestException("No data!");
        }
        if (user.getType() > User.ADMIN) {
            throw new PermissionDeniedException("Submission is closed");
        }
        BaseDataset dataset = DatasetUtils.apiGetById(postCase.dataset);
        if (!dataset.isEnabled() || dataset.isFinalJudge()) {
            throw new PermissionDeniedException("Dataset is disabled!");
        }
        if (!dataset.isSubmittable() && user.getType() > User.ADMIN) {
            throw new PermissionDeniedException("Dataset is not submittable!");
        }
        if (user.getType() == User.USER &&
                CaseUtils.countPreviousDay(user) >= CARPCase.DAILY_LIMIT) {
            throw new PermissionDeniedException("You have reached daily limits on submission!");
        }
        Binary archive = ArchiveUtils.convertSubmission(postCase.data, dataset.getEntry());
        BaseCase baseCase;
        switch (dataset.getType()) {
            case BaseDataset.CARP:
                baseCase = Database.getInstance().getCarpCases().insert(new CARPCase(user, (CARPDataset)dataset, archive));
                break;
            case BaseDataset.ISE:
                baseCase = Database.getInstance().getIseCases().insert(new ISECase(user, (ISEDataset)dataset, archive));
                break;
            case BaseDataset.IMP:
                baseCase = Database.getInstance().getImpCases().insert(new IMPCase(user, (IMPDataset)dataset, archive));
                break;
            default:
                throw new InvalidRequestException("Invalid dataset type!");
        }
        Database.getInstance().getLiteCases().insert(new LiteCase(baseCase));
        JudgeRunner.queue.add(baseCase);
        int remain = CARPCase.DAILY_LIMIT - CaseUtils.countPreviousDay(user);
        return new SubmitResponse(baseCase.getId(), remain);
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
