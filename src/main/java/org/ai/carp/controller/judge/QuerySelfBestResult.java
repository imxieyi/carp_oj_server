package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.CARPCase;

import java.util.ArrayList;
import java.util.List;

public class QuerySelfBestResult {

    private List<CARPCaseLite> carpCases;

    public QuerySelfBestResult(List<CARPCase> cases) {
        carpCases = new ArrayList<>();
        for (CARPCase c : cases) {
            carpCases.add(new CARPCaseLite(c));
        }
    }

    public List<CARPCaseLite> getCarpCases() {
        return carpCases;
    }
}
