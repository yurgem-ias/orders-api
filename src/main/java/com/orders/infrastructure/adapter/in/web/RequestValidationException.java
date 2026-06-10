package com.orders.infrastructure.adapter.in.web;

import org.springframework.validation.Errors;

public class RequestValidationException extends RuntimeException {
    private final Errors errors;

    public RequestValidationException(Errors errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}