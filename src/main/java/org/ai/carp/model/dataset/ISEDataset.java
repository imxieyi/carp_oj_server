package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datasets_ise")
public class ISEDataset extends BaseDataset {

    private String model;
    private String network;
    private String seeds;

    private double influence;

    @JsonIgnore
    public String getModel() {
        return model;
    }

    @JsonIgnore
    public String getNetwork() {
        return network;
    }

    @JsonIgnore
    public String getSeeds() {
        return seeds;
    }

    @JsonIgnore
    public double getInfluence() {
        return influence;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setSeeds(String seeds) {
        this.seeds = seeds;
    }

    private ISEDataset() {
        super();
    }

    public ISEDataset(String name, int time, int memory, int cpu, String model, String network, String seeds, double influence) {
        super(name, time, memory, cpu);
        this.model = model;
        this.network = network;
        this.seeds = seeds;
        this.influence = influence;
    }

    @Override
    public int getType() {
        return BaseDataset.ISE;
    }

    @Override
    public String getEntry() {
        return "ISE.py";
    }

    @Override
    public String toString() {
        return String.format("ISEDataset[id=%s, name=%s, model=%s]",
                id, name, model);
    }

}
