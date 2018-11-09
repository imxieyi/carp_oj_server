package org.ai.carp.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String message) {
        super(message);
    }
}
