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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Set<String> invalidUids = new HashSet<>();
        if (dataset.getType() == BaseDataset.CARP) {
            allBaseCases = Database.getInstance().getCarpCases()
                    .findCARPCasesByDatasetAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(
                            (CARPDataset)dataset, BaseCase.FINISHED, true)
                    .stream().filter(c -> c.getUser().getType() < User.ADMIN)
                    .map(c -> (BaseCase)c).collect(Collectors.toList());
            invalidUids = Database.getInstance().getCarpCases()
                    .findCARPCasesByDatasetAndStatusAndValidAndTimedout((CARPDataset)dataset, BaseCase.FINISHED, false, false)
                    .stream().filter(c -> c.getUser().getType() < User.ADMIN)
                    .map(BaseCase::getUserId).collect(Collectors.toSet());
        } else if (dataset.getType() == BaseDataset.ISE) {
            allBaseCases = Database.getInstance().getIseCases()
                    .findISECasesByDatasetAndStatusAndValidOrderByTimeAscSubmitTimeAsc(
                            (ISEDataset)dataset, BaseCase.FINISHED, true)
                    .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
            invalidUids = Database.getInstance().getIseCases()
                    .findISECasesByDatasetAndStatusAndValidAndTimedout((ISEDataset)dataset, BaseCase.FINISHED, false, false)
                    .stream().map(BaseCase::getUserId).collect(Collectors.toSet());
        } else if (dataset.getType() == BaseDataset.IMP) {
            allBaseCases = Database.getInstance().getImpCases()
                    .findIMPCasesByDatasetAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(
                            (IMPDataset)dataset, BaseCase.FINISHED, true)
                    .stream().filter(c -> c.getUser().getType() < User.ADMIN)
                    .map(c -> (BaseCase)c).collect(Collectors.toList());
            invalidUids = Database.getInstance().getImpCases()
                    .findIMPCasesByDatasetAndStatusAndValidAndTimedout((IMPDataset)dataset, BaseCase.FINISHED, false, false)
                    .stream().map(BaseCase::getUserId).collect(Collectors.toSet());
        }
        return new QueryTopResult(dataset, allBaseCases, invalidUids, user.getType() <= User.ADMIN);
    }

}
