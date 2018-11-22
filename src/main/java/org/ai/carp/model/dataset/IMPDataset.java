package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document(collection = "datasets")
public class IMPDataset extends BaseDataset {

    private String network;

    @JsonIgnore
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        if (StringUtils.isEmpty(id)) {
            this.network = network;
        }
    }

    private IMPDataset() {
        super();
    }

    public IMPDataset(String name, int time, int memory, int cpu, String network) {
        super(name, time, memory, cpu);
        this.network = network;
    }

    @Override
    public String toString() {
        return String.format("IMPDataset[id=%s, name=%s]",
                id, name);
    }

}
