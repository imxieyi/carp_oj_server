package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.BaseCase;

import java.util.ArrayList;
import java.util.List;

public class QuerySelfBestResult {

    private List<BaseCaseLite> baseCases;

    public QuerySelfBestResult(List<BaseCase> cases) {
        baseCases = new ArrayList<>();
        for (BaseCase c : cases) {
            baseCases.add(new BaseCaseLite(c));
        }
    }

    public List<BaseCaseLite> getBaseCases() {
        return baseCases;
    }
}
