package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/admin/judge/query")
public class QueryUserCaseController {

    @GetMapping
    public List<CARPCase> get(@RequestParam("user") String username, @RequestParam("dataset") String datasetname, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        User user = Database.getInstance().getUsers().findByUsername(username);
        if (user == null) {
            throw new InvalidRequestException("User does not exist!");
        }
        CARPDataset dataset = Database.getInstance().getCarpDatasets().findDatasetByName(datasetname);
        if (dataset == null) {
            throw new InvalidRequestException("CARPDataset does not exist!");
        }
        return Database.getInstance().getCarpCases().findCARPCasesByUserAndDatasetOrderBySubmitTimeDesc(user, dataset);
    }

}
