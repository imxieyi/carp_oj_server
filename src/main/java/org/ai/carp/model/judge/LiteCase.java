package org.ai.carp.model.judge;

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

    protected Date submitTime;

    protected String fullId;

    private LiteCase() {
    }

    public LiteCase(BaseCase fullCase) {
        this.user = fullCase.user;
        this.submitTime = fullCase.submitTime;
        this.fullId = fullCase.id;
    }

    public String getFullId() {
        return fullId;
    }
}
