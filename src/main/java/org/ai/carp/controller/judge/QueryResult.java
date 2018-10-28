package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.List;

class QueryResult {

    private List<CARPCase> carpCases;

    public QueryResult(List<CARPCase> carpCases) {
        this.carpCases = carpCases;
    }

    public List<CARPCase> getCarpCases() {
        return carpCases;
    }
}
