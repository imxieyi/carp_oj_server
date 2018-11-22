package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.controller.util.UserUtils;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/judge/best")
public class QuerySelfBestController {

    public static final int COUNT_BEST = 10;

    @GetMapping
    public QuerySelfBestResult get(@RequestParam("dataset") String did, HttpSession session) {
        User user = UserUtils.getUser(session, User.USER);
        if (StringUtils.isEmpty(did)) {
            throw new InvalidRequestException("No dataset id!");
        }
        Optional<CARPDataset> dataset = Database.getInstance().getCarpDatasets().findById(did);
        if (!dataset.isPresent()) {
            throw new InvalidRequestException("Invalid dataset!");
        }
        List<CARPCase> bestCarpCases = Database.getInstance().getCarpCases()
                .findCARPCasesByDatasetAndUserAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(dataset.get(), user, CARPCase.FINISHED, true, PageRequest.of(0, COUNT_BEST));
        return new QuerySelfBestResult(bestCarpCases);
    }

}
