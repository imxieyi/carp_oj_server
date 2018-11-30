package org.ai.carp.model.judge;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "cases_all")
public class LiteCase {

    @Id
    protected String id;

    @DBRef
    @Indexed
    protected User user;

    protected int type;

    protected Date submitTime;

    @Indexed
    protected String fullId;

    private LiteCase() {
    }

    public LiteCase(BaseCase fullCase) {
        this.user = fullCase.user;
        this.type = fullCase.getType();
        this.submitTime = fullCase.submitTime;
        this.fullId = fullCase.id;
    }

    public BaseCase getFullCase() {
        switch (type) {
            case BaseDataset.CARP:
                return Database.getInstance().getCarpCases().findById(fullId).orElse(null);
            case BaseDataset.ISE:
                return Database.getInstance().getIseCases().findById(fullId).orElse(null);
            case BaseDataset.IMP:
                return Database.getInstance().getImpCases().findById(fullId).orElse(null);
            default:
                return null;
        }
    }
}
