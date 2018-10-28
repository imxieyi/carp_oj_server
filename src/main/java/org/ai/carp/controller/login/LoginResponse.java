package org.ai.carp.controller.login;

import org.ai.carp.controller.util.ResponseBase;

class LoginResponse extends ResponseBase {

    private String uid;

    LoginResponse(String reason) {
        this(false, reason, null);
    }

    LoginResponse(boolean ok, String reason, String uid) {
        super(ok, reason);
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
