package org.ai.carp.controller.judge;

import org.ai.carp.model.judge.BaseCase;

import java.util.Date;

public class BaseCaseLite {

    private String userName;
    private Date submitTime;
    private double time;
    private double result;

    // For final judge
    private int rank;
    private int count;

    public BaseCaseLite(BaseCase baseCase) {
        this.userName = baseCase.getUser().getUsername();
        this.result = baseCase.getResult();
        this.submitTime = baseCase.getSubmitTime();
        this.time = baseCase.getTime();
        this.count = 1;
    }

    public BaseCaseLite(BaseCase baseCase, int rank) {
        this(baseCase);
        this.rank = rank;
    }

    // For final judge
    public BaseCaseLite(String userName, double time, double result) {
        this.userName = userName;
        this.submitTime = new Date(0L);
        this.time = time;
        this.result = result;
        this.count = 1;
    }

    public void addResult(BaseCase baseCase) {
        this.time += baseCase.getTime();
        this.result += baseCase.getResult();
        this.count++;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUserName() {
        return userName;
    }

    public double getResult() {
        return result / (double) count;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public double getTime() {
        return time / (double) count;
    }

    public int getCount() {
        return count;
    }

    public int getRank() {
        return rank;
    }
}
