package org.ai.carp.controller.login;

class LoginResponse {

    private boolean ok;
    private String reason;
    private String uid;

    LoginResponse(String reason) {
        this(false, reason, "");
    }

    LoginResponse(boolean ok, String reason, String uid) {
        this.ok = ok;
        this.reason = reason;
        this.uid = uid;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public String getUid() {
        return uid;
    }
}
