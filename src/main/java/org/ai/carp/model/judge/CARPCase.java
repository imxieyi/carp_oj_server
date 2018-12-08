package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.CARPDataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Document(collection = "cases_carp")
public class CARPCase extends BaseCase {

    private static Random random = new Random();

    // Submission
    @DBRef
    @Indexed
    private CARPDataset dataset;

    // Result
    private String path;
    private int cost;

    public CARPCase(User user, CARPDataset dataset, Binary archive) {
        super(user, archive);
        this.dataset = dataset;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @JsonIgnore
    public CARPDataset getDataset() {
        return dataset;
    }

    @Override
    public int getType() {
        return BaseDataset.CARP;
    }

    public String getDatasetName() {
        return dataset.getName();
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    @JsonIgnore
    public int getCost() {
        return cost;
    }

    @Override
    public double getResult() {
        return cost;
    }

    @Override
    public void setResult(double result) {
        cost = (int) Math.round(result);
    }

    @Override
    protected String buildConfig() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("entry", "CARP_solver.py");
        node.put("data", "data.dat");
        node.put("parameters", "$data -t $time -s $seed");
        node.put("time", dataset.getTime());
        node.put("memory", dataset.getMemory());
        node.put("cpu", dataset.getCpu());
        int seed = random.nextInt();
        if (seed == Integer.MIN_VALUE) {
            seed++;
        }
        node.put("seed", Math.abs(seed));
        return mapper.writeValueAsString(node);
    }

    @Override
    protected void buildDataset(ObjectNode node) {
    }

    @Override
    protected void writeData(ZipOutputStream zos) throws IOException {
        ZipEntry data = new ZipEntry("data/data.dat");
        zos.putNextEntry(data);
        zos.write(dataset.getData().getBytes());
        zos.closeEntry();
    }

    @Override
    public String toString() {
        return String.format("CARPCase[id=%s, user=%s, dataset=%s, status=%d, cost=%d]",
                id, user.getUsername(), dataset.getName(), status, cost);
    }
}
