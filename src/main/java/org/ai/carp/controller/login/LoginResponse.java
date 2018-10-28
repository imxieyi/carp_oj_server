package org.ai.carp.controller.login;

import org.ai.carp.controller.ResponseBase;

class LoginResponse extends ResponseBase {

    private String uid;

    LoginResponse(String reason) {
        this(false, reason, "");
    }

    LoginResponse(boolean ok, String reason, String uid) {
        super(ok, reason);
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
