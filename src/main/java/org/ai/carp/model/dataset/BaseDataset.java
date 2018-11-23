package org.ai.carp.model.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class BaseDataset {

    public static final int CARP = 0;
    public static final int ISE = 1;
    public static final int IMP = 2;

    @Id
    protected String id;

    @Indexed(unique = true)
    protected String name;
    protected int time;
    protected int memory;
    protected int cpu;

    public String getId() {
        return id;
    }

    public abstract int getType();

    @JsonIgnore
    public abstract String getEntry();

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

    BaseDataset() {
    }

    BaseDataset(String name, int time, int memory, int cpu) {
        name = name.replaceAll(" ", "");
        this.name = name;
        this.time = time;
        this.memory = memory;
        this.cpu = cpu;
    }

    @Override
    public String toString() {
        return String.format("BaseDataset[id=%s, name=%s]",
                id, name);
    }

}
