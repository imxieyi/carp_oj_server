package org.ai.carp.controller;

import org.ai.carp.controller.util.ResponseBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RestController
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseBase handler(HttpServletRequest request, Exception e) {
        logger.error("[{}] {}", request.getRemoteHost(),e.getMessage());
        return new ResponseBase(e.getMessage());
    }

}
