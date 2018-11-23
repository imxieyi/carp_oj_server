package org.ai.carp.model.judge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.dataset.ISEDataset;
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
public class ISECase extends BaseCase {

    private static Random random = new Random();

    // Submission
    @DBRef
    @Indexed
    private ISEDataset dataset;

    // Result
    private double influence;

    public ISECase(User user, ISEDataset dataset, Binary archive) {
        super(user, archive);
        this.dataset = dataset;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

    @JsonIgnore
    public ISEDataset getDataset() {
        return dataset;
    }

    @Override
    public int getType() {
        return BaseDataset.ISE;
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
        node.put("entry", "ISE.py");
        node.put("network", "network.dat");
        node.put("seeds", "seeds.dat");
        node.put("parameters", "-i $network -s $seeds -m $model -t $time");
        node.put("time", dataset.getTime());
        node.put("memory", dataset.getMemory());
        node.put("cpu", dataset.getCpu());
        node.put("model", dataset.getModel());
        return mapper.writeValueAsString(node);
    }

    @Override
    protected void writeData(ZipOutputStream zos) throws IOException {
        ZipEntry data = new ZipEntry("data/network.dat");
        zos.putNextEntry(data);
        zos.write(dataset.getNetwork().getBytes());
        zos.closeEntry();
        data = new ZipEntry("data/seeds.dat");
        zos.putNextEntry(data);
        zos.write(dataset.getSeeds().getBytes());
        zos.closeEntry();
    }

    @Override
    public String toString() {
        return String.format("ISECase[id=%s, user=%s, dataset=%s, status=%d, influence=%f]",
                id, user.getUsername(), dataset.getName(), status, influence);
    }
}
