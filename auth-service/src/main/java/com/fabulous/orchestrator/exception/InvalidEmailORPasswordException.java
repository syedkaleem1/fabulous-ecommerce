package com.fabulous.orchestrator.exception;

public class InvalidEmailORPasswordException extends RuntimeException {
    public InvalidEmailORPasswordException(String message) {
        super(message);
    }
}
