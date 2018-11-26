package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class QuerySelfBestResult {

    private List<BaseCaseLite> baseCases;

    public QuerySelfBestResult(List<BaseCase> cases) {
        baseCases = new ArrayList<>();
        for (int i=0; i<cases.size(); i++) {
            baseCases.add(new BaseCaseLite(cases.get(i), i + 1));
        }
    }

    public QuerySelfBestResult(List<BaseCaseLite> baseCases, User user) {
        this.baseCases = new ArrayList<>();
        for (int i=0; i<baseCases.size(); i++) {
            BaseCaseLite caseLite = baseCases.get(i);
            if (caseLite.getUserName().equals(user.getUsername())) {
                caseLite.setRank(i + 1);
                this.baseCases.add(caseLite);
                return;
            }
        }
    }

    public List<BaseCaseLite> getBaseCases() {
        return baseCases;
    }
}
