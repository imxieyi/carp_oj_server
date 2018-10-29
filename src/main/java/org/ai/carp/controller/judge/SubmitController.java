package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.ArchiveUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.Dataset;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/judge/submit")
public class SubmitController {

    @PostMapping
    public SubmitResponse post(@RequestBody PostCase postCase, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(postCase.dataset)) {
            throw new InvalidRequestException("No dataset!");
        }
        if (StringUtils.isEmpty(postCase.data)) {
            throw new InvalidRequestException("No data!");
        }
        Optional<Dataset> dataset = Database.getInstance().getDatasets().findById(postCase.dataset);
        if (!dataset.isPresent()) {
            throw new InvalidRequestException("Invalid dataset!");
        }
        Binary archive = ArchiveUtils.convertSubmission(postCase.data);
        CARPCase carpCase = Database.getInstance().getCarpCases().insert(new CARPCase(user, dataset.get(), archive));
        JudgeRunner.queue.add(carpCase);
        return new SubmitResponse(carpCase.getId());
    }

}

class SubmitResponse {

    private String cid;

    public SubmitResponse(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }
}

class PostCase {
    public String dataset;
    public String data;
}
