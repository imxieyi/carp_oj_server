package org.ai.carp.model.judge;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.dataset.OldDataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "cases")
public class OldCARPCase {
    @Id
    private String id;

    @DBRef
    @Indexed
    private User user;

    // Submission
    @DBRef
    @Indexed
    private OldDataset dataset;
    private Binary archive;
    private int status;
    private Date submitTime;
    private Date judgeTime;
    @DBRef
    private User judgeWorker;

    // Result
    private boolean timedout;
    private String stdout;
    private boolean outOverflow;
    private String stderr;
    private boolean errOverflow;
    private double time;
    private int exitcode;
    private String path;
    private boolean valid;
    private int cost;
    private String reason;

    public OldDataset getDataset() {
        return dataset;
    }

    public CARPCase convert(CARPDataset dataset) {
        CARPCase carpCase = new CARPCase(user, dataset, archive);
        carpCase.setStatus(status);
        carpCase.setSubmitTime(submitTime);
        carpCase.setJudgeTime(judgeTime);
        carpCase.setJudgeWorker(judgeWorker);
        carpCase.setTimedout(timedout);
        carpCase.setStdout(stdout);
        carpCase.setOutOverflow(outOverflow);
        carpCase.setStderr(stderr);
        carpCase.setErrOverflow(errOverflow);
        carpCase.setTime(time);
        carpCase.setExitcode(exitcode);
        carpCase.setPath(path);
        carpCase.setValid(valid);
        carpCase.setCost(cost);
        carpCase.setReason(reason);
        carpCase = Database.getInstance().getCarpCases().insert(carpCase);
        LiteCase liteCase = new LiteCase(carpCase);
        Database.getInstance().getLiteCases().insert(liteCase);
        return carpCase;
    }

    @Override
    public String toString() {
        return String.format("OldCARPCase[id=%s, user=%s, dataset=%s, status=%d, cost=%d]",
                id, user.getUsername(), dataset.getName(), status, cost);
    }
}