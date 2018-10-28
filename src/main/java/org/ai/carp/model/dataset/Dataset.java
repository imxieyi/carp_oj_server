package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datasets")
public class Dataset {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private int time;
    private int memory;
    private int cpu;
    private String data;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getMemory() {
        return memory;
    }

    public int getCpu() {
        return cpu;
    }

    @JsonIgnore
    public String getData() {
        return data;
    }

    private Dataset() {
    }

    public Dataset(String name, int time, int memory, int cpu, String data) {
        this.name = name;
        this.time = time;
        this.memory = memory;
        this.cpu = cpu;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Dataset[id=%s, name=%s]",
                id, name);
    }

}
