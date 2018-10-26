package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "cases")
public class CARPCase {

    // Status
    public static final int WAITING = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;

    @Id
    private String id;

    private User user;

    // Submission
    private Dataset dataset;
    private Binary archive;
    private int status;
    private Date submitTime;
    private Date judgeTime;

    // Result
    private boolean timedout;
    private String stdout;
    private String stderr;
    private double time;
    private int exitcode;

    public CARPCase(User user, Dataset dataset, Binary archive) {
        this.user = user;
        this.dataset = dataset;
        this.archive = archive;
        this.submitTime = new Date();
        this.status = WAITING;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setJudgeTime(Date judge_time) {
        this.judgeTime = judge_time;
    }

    public void setTimedout(boolean timedout) {
        this.timedout = timedout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setExitcode(int exitcode) {
        this.exitcode = exitcode;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Dataset getDataset() {
        return dataset;
    }

    @JsonIgnore
    public Binary getArchive() {
        return archive;
    }

    public int getStatus() {
        return status;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public Date getJudgeTime() {
        return judgeTime;
    }

    public boolean isTimedout() {
        return timedout;
    }

    @JsonIgnore
    public String getStdout() {
        return stdout;
    }

    @JsonIgnore
    public String getStderr() {
        return stderr;
    }

    public double getTime() {
        return time;
    }

    public int getExitcode() {
        return exitcode;
    }

    @Override
    public String toString() {
        return String.format("CARPCase[id=%s, user=%s, dataset=%s, status=%d]",
                id, user.getUsername(), dataset.getName(), status);
    }
}
