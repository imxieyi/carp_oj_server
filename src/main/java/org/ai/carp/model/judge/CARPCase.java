package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ai.carp.controller.websocket.WebsocketHandler;
import org.ai.carp.model.dataset.Dataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Document(collection = "cases")
public class CARPCase {

    // Status
    public static final int WAITING = 0;
    public static final int QUEUED = 1;
    public static final int RUNNING = 2;
    public static final int FINISHED = 3;

    @Id
    private String id;

    @DBRef
    @Indexed
    private User user;

    // Submission
    @DBRef
    @Indexed
    private Dataset dataset;
    private Binary archive;
    private int status;
    private Date submitTime;
    private Date judgeTime;

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

    public void setPath(String path) {
        this.path = path;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public String getUserName() {
        return user.getUsername();
    }

    @JsonIgnore
    public Dataset getDataset() {
        return dataset;
    }

    public String getDatasetName() {
        return dataset.getName();
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

    public String getPath() {
        return path;
    }

    public boolean isValid() {
        return valid;
    }

    public int getCost() {
        return cost;
    }

    private String buildConfig() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("entry", "CARP_solver.py");
        node.put("data", "data.dat");
        node.put("parameters", "$data -t $time");
        node.put("time", dataset.getTime());
        node.put("memory", dataset.getMemory());
        node.put("cpu", dataset.getCpu());
        return mapper.writeValueAsString(node);
    }

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
        ZipEntry data = new ZipEntry("data/data.dat");
        zos.putNextEntry(data);
        zos.write(dataset.getData().getBytes());
        zos.closeEntry();
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
        dataNode.put("jid", id);
        dataNode.put("data", encodedArchive);
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", WebsocketHandler.CASE_DATA);
        rootNode.put("payload", dataNode);
        return mapper.writeValueAsString(rootNode);
    }

    @Override
    public String toString() {
        return String.format("CARPCase[id=%s, user=%s, dataset=%s, status=%d, cost=%d]",
                id, user.getUsername(), dataset.getName(), status, cost);
    }
}
