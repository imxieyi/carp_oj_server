package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datasets_imp")
public class IMPDataset extends BaseDataset {

    private int seedCount;
    private String model;
    private String network;

    @JsonIgnore
    public int getSeedCount() {
        return seedCount;
    }

    @JsonIgnore
    public String getModel() {
        return model;
    }

    @JsonIgnore
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    private IMPDataset() {
        super();
    }

    public IMPDataset(String name, int time, int memory, int cpu, int seedCount, String model, String network) {
        super(name, time, memory, cpu);
        this.seedCount = seedCount;
        this.model = model;
        this.network = network;
    }

    @Override
    public int getType() {
        return BaseDataset.IMP;
    }

    @Override
    public String getEntry() {
        return "IMP.py";
    }

    @Override
    public String toString() {
        return String.format("IMPDataset[id=%s, name=%s, model=%s]",
                id, name, model);
    }

}
