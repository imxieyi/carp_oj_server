package org.ai.carp.controller;

public abstract class ResponseBase {

    private boolean ok;
    private String reason;

    protected ResponseBase(boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }
}
