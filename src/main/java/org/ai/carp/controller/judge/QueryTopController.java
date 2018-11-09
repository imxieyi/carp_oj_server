package org.ai.carp.controller.judge;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.judge.CARPCase;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/judge/top")
public class QueryTopController {

    public static final int COUNT_LEADERBOARD = 20;

    @GetMapping
    public QueryTopResult get(@RequestParam("dataset") String did, HttpSession session) {
        if (StringUtils.isEmpty(did)) {
            throw new InvalidRequestException("No dataset id!");
        }
        Optional<Dataset> dataset = Database.getInstance().getDatasets().findById(did);
        if (!dataset.isPresent()) {
            throw new InvalidRequestException("Invalid dataset!");
        }
        List<CARPCase> allCarpCases = Database.getInstance().getCarpCases()
                .findCARPCasesByDatasetAndStatusAndValidOrderByCostAscTimeAscSubmitTimeAsc(dataset.get(), CARPCase.FINISHED, true);
        return new QueryTopResult(allCarpCases);
    }

}
