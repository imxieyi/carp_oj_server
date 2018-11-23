package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ai.carp.controller.websocket.WebsocketHandler;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class BaseCase {

    // Status
    public static final int WAITING = 0;
    public static final int QUEUED = 1;
    public static final int RUNNING = 2;
    public static final int FINISHED = 3;
    public static final int ERROR = 4;

    public static final int DAILY_LIMIT = 30;
    public static final int RUN_COUNT = 5;

    @Id
    protected String id;

    @DBRef
    @Indexed
    protected User user;

    // Submission
    protected Binary archive;
    protected int status;
    protected Date submitTime;
    protected Date judgeTime;
    @DBRef
    protected User judgeWorker;

    // Result
    protected boolean timedout;
    protected String stdout;
    protected boolean outOverflow;
    protected String stderr;
    protected boolean errOverflow;
    protected double time;
    protected int exitcode;
    protected boolean valid;
    protected String reason;

    protected BaseCase(User user, Binary archive) {
        this.user = user;
        this.archive = archive;
        this.submitTime = new Date();
        this.status = WAITING;
    }

    public abstract int getType();

    public void setStatus(int status) {
        this.status = status;
    }

    void setSubmitTime(Date submitTime) {
        if (StringUtils.isEmpty(id)) {
            this.submitTime = submitTime;
        }
    }

    public void setJudgeTime(Date judge_time) {
        this.judgeTime = judge_time;
    }

    public void setJudgeWorker(User judgeWorker) {
        this.judgeWorker = judgeWorker;
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

    public void setOutOverflow(boolean outOverflow) {
        this.outOverflow = outOverflow;
    }

    public void setErrOverflow(boolean errOverflow) {
        this.errOverflow = errOverflow;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setExitcode(int exitcode) {
        this.exitcode = exitcode;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public String getUserId() {
        return user.getId();
    }

    @JsonIgnore
    public BaseDataset getBaseDataset() {
        if (this instanceof CARPCase) {
            return ((CARPCase) this).getDataset();
        }
        if (this instanceof ISECase) {
            return ((ISECase) this).getDataset();
        }
        if (this instanceof IMPCase) {
            return ((IMPCase) this).getDataset();
        }
        return null;
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

    @JsonIgnore
    public User getJudgeWorker() {
        return judgeWorker;
    }

    public String getWorkerName() {
        if (judgeWorker == null) {
            return null;
        }
        return judgeWorker.getUsername();
    }

    public boolean isTimedout() {
        return timedout;
    }

    @JsonIgnore
    public String getStdout() {
        return stdout;
    }

    public boolean isOutOverflow() {
        return outOverflow;
    }

    @JsonIgnore
    public String getStderr() {
        return stderr;
    }

    public boolean isErrOverflow() {
        return errOverflow;
    }

    public double getTime() {
        return time;
    }

    public int getExitcode() {
        return exitcode;
    }

    public boolean isValid() {
        return valid;
    }

    public abstract double getResult();

    public String getReason() {
        return reason;
    }

    protected abstract String buildConfig() throws JsonProcessingException;

    protected abstract void buildDataset(ObjectNode node);

    protected abstract void writeData(ZipOutputStream zos) throws IOException;

    @JsonIgnore
    public String getWorkerJson() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(archive.getData());
        ZipInputStream zis = new ZipInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        // Write config
        ZipEntry config = new ZipEntry("config.json");
        zos.putNextEntry(config);
        zos.write(buildConfig().getBytes());
        zos.closeEntry();
        // Write data
        writeData(zos);
        // Copy program
        ZipEntry origin;
        byte[] buffer = new byte[2048];
        while ((origin = zis.getNextEntry()) != null) {
            ZipEntry target = new ZipEntry("program/" + origin.getName());
            zos.putNextEntry(target);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
        }
        zis.close();
        zos.close();
        // Encode to json
        String encodedArchive = Base64.getEncoder().encodeToString(baos.toByteArray());
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("cid", id);
        dataNode.put("type", getType());
        dataNode.put("data", encodedArchive);
        ObjectNode datasetNode = mapper.createObjectNode();
        buildDataset(datasetNode);
        dataNode.put("dataset", datasetNode);
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", WebsocketHandler.CASE_DATA);
        rootNode.put("payload", dataNode);
        return mapper.writeValueAsString(rootNode);
    }

    @Override
    public String toString() {
        return String.format("BaseCase[id=%s, user=%s, status=%d]",
                id, user.getUsername(), status);
    }
}
