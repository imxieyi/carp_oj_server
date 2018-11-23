package org.ai.carp.controller.admin.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.DatasetUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/judge/query")
public class QueryUserCaseController {

    @GetMapping
    public List<BaseCase> get(@RequestParam("user") String username, @RequestParam("dataset") String datasetname, HttpSession session) {
        UserUtils.getUser(session, User.ADMIN);
        User user = Database.getInstance().getUsers().findByUsername(username);
        if (user == null) {
            throw new InvalidRequestException("User does not exist!");
        }
        BaseDataset dataset = DatasetUtils.findByName(datasetname);
        if (dataset == null) {
            throw new InvalidRequestException("Dataset does not exist!");
        }
        List<BaseCase> baseCases = new ArrayList<>();
        switch (dataset.getType()) {
            case BaseDataset.CARP:
                baseCases = Database.getInstance().getCarpCases()
                        .findCARPCasesByUserAndDatasetOrderBySubmitTimeDesc(user, (CARPDataset)dataset)
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            case BaseDataset.ISE:
                baseCases = Database.getInstance().getIseCases()
                        .findISECasesByUserAndDatasetOrderBySubmitTimeDesc(user, (ISEDataset)dataset)
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            case BaseDataset.IMP:
                baseCases = Database.getInstance().getImpCases()
                        .findIMPCasesByUserAndDatasetOrderBySubmitTimeDesc(user, (IMPDataset) dataset)
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            default:
        }
        return baseCases;
    }

}
