package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.ArrayList;
import java.util.List;

public class QuerySelfBestResult {

    private List<BaseCaseLite> carpCases;

    public QuerySelfBestResult(List<CARPCase> cases) {
        carpCases = new ArrayList<>();
        for (CARPCase c : cases) {
            carpCases.add(new BaseCaseLite(c));
        }
    }

    public List<BaseCaseLite> getCarpCases() {
        return carpCases;
    }
}
