package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.List;

class QueryResult {

    private List<CARPCase> carpCases;
    private int total;

    public QueryResult(List<CARPCase> carpCases, int total) {
        this.carpCases = carpCases;
        this.total = total;
    }

    public List<CARPCase> getCarpCases() {
        return carpCases;
    }

    public int getTotal() {
        return total;
    }
}
