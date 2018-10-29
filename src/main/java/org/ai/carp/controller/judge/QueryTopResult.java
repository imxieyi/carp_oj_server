package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryTopResult {

    private List<CARPCaseLite> carpCases;

    public QueryTopResult(List<CARPCase> cases) {
        carpCases = new ArrayList<>();
        for (CARPCase c : cases) {
            carpCases.add(new CARPCaseLite(c));
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
