package org.ai.carp.controller.util;

public class ResponseBase {

    private boolean ok;
    private String reason;

    public ResponseBase(String reason) {
        this(false, reason);
    }

    public ResponseBase(boolean ok, String reason) {
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
