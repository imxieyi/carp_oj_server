package org.ai.carp.controller.login;

class LoginResponse {

    private String uid;

    LoginResponse(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
