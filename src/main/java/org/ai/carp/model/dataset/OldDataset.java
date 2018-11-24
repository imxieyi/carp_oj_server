package org.ai.carp.model.dataset;

import org.ai.carp.model.Database;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datasets")
public class OldDataset {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private int time;
    private int memory;
    private int cpu;
    private String data;

    private OldDataset() {
    }

    public String getName() {
        return name;
    }

    public CARPDataset convert() {
        CARPDataset dataset = new CARPDataset(name, time, memory, cpu, data);
        dataset.setEnabled(false);
        dataset.setSubmittable(false);
        return Database.getInstance().getCarpDatasets().insert(dataset);
    }

    @Override
    public String toString() {
        return String.format("OldDataset[id=%s, name=%s]",
                id, name);
    }

}