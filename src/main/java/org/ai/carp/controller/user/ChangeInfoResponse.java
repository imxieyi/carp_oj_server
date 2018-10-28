package org.ai.carp.controller.user;

import org.ai.carp.controller.ResponseBase;

public class ChangeInfoResponse extends ResponseBase {

    public ChangeInfoResponse(String reason) {
        this(false, reason);
    }

    public ChangeInfoResponse(boolean ok, String reason) {
        super(ok, reason);
    }

}