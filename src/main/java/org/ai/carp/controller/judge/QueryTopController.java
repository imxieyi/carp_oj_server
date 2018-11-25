package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.PermissionDeniedException;
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
@RequestMapping("/api/judge/top")
public class QueryTopController {

    public static final int COUNT_LEADERBOARD = 20;

    @GetMapping
    public QueryTopResult get(@RequestParam("dataset") String did, HttpSession session) {
        User user = UserUtils.getUser(session, User.MAX);
        BaseDataset dataset = DatasetUtils.apiGetById(did);
        if (dataset.isFinalJudge() && user.getType() > User.ADMIN) {
            throw new PermissionDeniedException("Permission denied!");
        }
        List<BaseCase> allBaseCases = new ArrayList<>();
        if (dataset.getType() == BaseDataset.CARP) {
            allBaseCases = Database.getInstance().getCarpCases()
                    .findCARPCasesByDatasetAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(
                            (CARPDataset)dataset, BaseCase.FINISHED, true)
                    .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
        } else if (dataset.getType() == BaseDataset.ISE) {
            allBaseCases = Database.getInstance().getIseCases()
                    .findISECasesByDatasetAndStatusAndValidOrderByTimeAscSubmitTimeAsc(
                            (ISEDataset)dataset, BaseCase.FINISHED, true)
                    .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
        } else if (dataset.getType() == BaseDataset.IMP) {
            allBaseCases = Database.getInstance().getImpCases()
                    .findIMPCasesByDatasetAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(
                            (IMPDataset)dataset, BaseCase.FINISHED, true)
                    .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
        }
        return new QueryTopResult(dataset, allBaseCases, user.getType() <= User.ADMIN);
    }

}
