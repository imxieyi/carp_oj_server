package org.ai.carp.controller.judge;

import org.ai.carp.controller.util.DatasetUtils;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.dataset.ISEDataset;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/judge/best")
public class QuerySelfBestController {

    public static final int COUNT_BEST = 10;

    @GetMapping
    public QuerySelfBestResult get(@RequestParam("dataset") String did, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        BaseDataset dataset = DatasetUtils.apiGetById(did);
        if (dataset.isFinalJudge()) {
            return new QuerySelfBestResult(QueryTopResult.getFinalList(dataset.getId()), user);
        }
        List<BaseCase> bestCases = new ArrayList<>();
        switch (dataset.getType()) {
            case BaseDataset.CARP:
                bestCases = Database.getInstance().getCarpCases()
                        .findCARPCasesByDatasetAndUserAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(
                                (CARPDataset)dataset, user, BaseCase.FINISHED, true,
                                PageRequest.of(0, COUNT_BEST))
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            case BaseDataset.ISE:
                bestCases = Database.getInstance().getIseCases()
                        .findISECasesByDatasetAndUserAndStatusAndValidOrderByTimeAscSubmitTimeAsc(
                                (ISEDataset)dataset, user, BaseCase.FINISHED, true,
                                PageRequest.of(0, COUNT_BEST))
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            case BaseDataset.IMP:
                bestCases = Database.getInstance().getImpCases()
                        .findIMPCasesByDatasetAndUserAndStatusAndValidOrderByInfluenceDescTimeAscSubmitTimeAsc(
                                (IMPDataset) dataset, user, BaseCase.FINISHED, true,
                                PageRequest.of(0, COUNT_BEST))
                        .stream().map(c -> (BaseCase)c).collect(Collectors.toList());
                break;
            default:
        }
        return new QuerySelfBestResult(bestCases);
    }

}
