package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document(collection = "datasets_carp")
public class CARPDataset extends BaseDataset {

    private String data;

    @JsonIgnore
    public String getData() {
        return data;
    }

    public void setData(String data) {
        if (StringUtils.isEmpty(id)) {
            this.data = data;
        }
    }

    private CARPDataset() {
    }

    public CARPDataset(String name, int time, int memory, int cpu, String data) {
        super(name, time, memory, cpu);
        this.data = data;
    }

    @Override
    public int getType() {
        return BaseDataset.CARP;
    }

    @Override
    public String toString() {
        return String.format("CARPDataset[id=%s, name=%s]",
                id, name);
    }

}
