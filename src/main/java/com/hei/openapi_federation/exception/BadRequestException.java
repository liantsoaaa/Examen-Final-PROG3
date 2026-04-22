package com.hei.openapi_federation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(HttpStatus status, String message) {
        super(status, message);
    }
}