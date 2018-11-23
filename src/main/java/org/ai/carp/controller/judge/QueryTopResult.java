package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.BaseCase;

import java.util.*;

public class QueryTopResult {

    private List<BaseCaseLite> baseCases;

    public QueryTopResult(List<BaseCase> cases, boolean admin) {
        baseCases = new ArrayList<>();
        Set<String> uids = new HashSet<>();
        for (BaseCase c : cases) {
//            if (c.getUser().getType() != User.USER) {
//                continue;
//            }
            if (!uids.contains(c.getUser().getId())) {
                baseCases.add(new BaseCaseLite(c));
                uids.add(c.getUser().getId());
                if (!admin && baseCases.size() >= QueryTopController.COUNT_LEADERBOARD) {
                    break;
                }
            }
        }
    }

    public List<BaseCaseLite> getBaseCases() {
        return baseCases;
    }
}

class BaseCaseLite {

    private String userName;
    private String datasetId;
    private Date submitTime;
    private double time;
    private double result;

    public BaseCaseLite(BaseCase baseCase) {
        this.userName = baseCase.getUser().getUsername();
        this.datasetId = baseCase.getId();
        this.result = baseCase.getResult();
        this.submitTime = baseCase.getSubmitTime();
        this.time = baseCase.getTime();
    }

    public String getUserName() {
        return userName;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public double getResult() {
        return result;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public double getTime() {
        return time;
    }
}
