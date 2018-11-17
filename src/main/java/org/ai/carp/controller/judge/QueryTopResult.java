package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.user.User;

import java.util.*;

public class QueryTopResult {

    private List<CARPCaseLite> carpCases;

    public QueryTopResult(List<CARPCase> cases, boolean admin) {
        carpCases = new ArrayList<>();
        Set<String> uids = new HashSet<>();
        for (CARPCase c : cases) {
            if (c.getUser().getType() != User.USER) {
                continue;
            }
            if (!uids.contains(c.getUser().getId())) {
                carpCases.add(new CARPCaseLite(c));
                uids.add(c.getUser().getId());
                if (!admin && carpCases.size() >= QueryTopController.COUNT_LEADERBOARD) {
                    break;
                }
            }
        }
    }

    public List<CARPCaseLite> getCarpCases() {
        return carpCases;
    }
}

class CARPCaseLite {

    private String userName;
    private String datasetId;
    private Date submitTime;
    private double time;
    private int cost;

    public CARPCaseLite(CARPCase carpCase) {
        this.userName = carpCase.getUser().getUsername();
        this.datasetId = carpCase.getId();
        this.cost = carpCase.getCost();
        this.submitTime = carpCase.getSubmitTime();
        this.time = carpCase.getTime();
    }

    public String getUserName() {
        return userName;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public int getCost() {
        return cost;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public double getTime() {
        return time;
    }
}
