package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.IMPDataset;
import org.ai.carp.model.user.User;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Document(collection = "cases_ise")
public class IMPCase extends BaseCase {

    private static Random random = new Random();

    // Submission
    @DBRef
    @Indexed
    private IMPDataset dataset;

    // Result
    private double influence;

    public IMPCase(User user, IMPDataset dataset, Binary archive) {
        super(user, archive);
        this.dataset = dataset;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

    @JsonIgnore
    public IMPDataset getDataset() {
        return dataset;
    }

    public int getType() {
        return BaseDataset.IMP;
    }

    public String getDatasetId() {
        return dataset.getId();
    }

    public double getInfluence() {
        return influence;
    }

    @Override
    protected String buildConfig() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("entry", "IMP.py");
        node.put("network", "network.dat");
        node.put("parameters", "-i $network -k $seedCount -m $model -t $time");
        node.put("time", dataset.getTime());
        node.put("memory", dataset.getMemory());
        node.put("cpu", dataset.getCpu());
        node.put("seedCount", dataset.getSeedCount());
        node.put("model", dataset.getModel());
        return mapper.writeValueAsString(node);
    }

    @Override
    protected void writeData(ZipOutputStream zos) throws IOException {
        ZipEntry data = new ZipEntry("data/network.dat");
        zos.putNextEntry(data);
        zos.write(dataset.getNetwork().getBytes());
        zos.closeEntry();
    }

    @Override
    public String toString() {
        return String.format("ISECase[id=%s, user=%s, dataset=%s, status=%d, influence=%d]",
                id, user.getUsername(), dataset.getName(), status, influence);
    }
}
