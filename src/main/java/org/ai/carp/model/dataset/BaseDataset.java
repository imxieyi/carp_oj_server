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

    protected boolean enabled;
    protected boolean submittable;
    protected boolean finalJudge;

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

    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSubmittable() {
        return submittable;
    }

    @JsonIgnore
    public boolean isFinalJudge() {
        return finalJudge;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }

    public void setFinalJudge(boolean finalJudge) {
        this.finalJudge = finalJudge;
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
