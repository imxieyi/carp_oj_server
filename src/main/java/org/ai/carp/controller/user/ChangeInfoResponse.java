package org.ai.carp.controller.user;

public class ChangeInfoResponse {

    private boolean ok;
    private String reason;

    ChangeInfoResponse(String reason) {
        this(false, reason);
    }

    ChangeInfoResponse(boolean ok, String reason) {
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