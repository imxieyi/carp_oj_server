package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.BaseCase;

import java.util.List;

class QueryResult {

    private List<BaseCase> baseCases;
    private int total;

    public QueryResult(List<BaseCase> baseCases, int total) {
        this.baseCases = baseCases;
        this.total = total;
    }

    public List<BaseCase> getBaseCases() {
        return baseCases;
    }

    public int getTotal() {
        return total;
    }
}
