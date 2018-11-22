package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document(collection = "datasets")
public class ISEDataset extends BaseDataset {

    private String network;
    private String seeds;

    @JsonIgnore
    public String getNetwork() {
        return network;
    }

    @JsonIgnore
    public String getSeeds() {
        return seeds;
    }

    public void setNetwork(String network) {
        if (StringUtils.isEmpty(id)) {
            this.network = network;
        }
    }

    public void setSeeds(String seeds) {
        if (StringUtils.isEmpty(id)) {
            this.seeds = seeds;
        }
    }

    private ISEDataset() {
        super();
    }

    public ISEDataset(String name, int time, int memory, int cpu, String network, String seeds) {
        super(name, time, memory, cpu);
        this.network = network;
        this.seeds = seeds;
    }

    @Override
    public String toString() {
        return String.format("ISEDataset[id=%s, name=%s]",
                id, name);
    }

}
