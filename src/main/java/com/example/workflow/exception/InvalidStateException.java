package com.example.workflow.exception;

public class InvalidStateException extends WorkflowException {
    public InvalidStateException(String message) {
        super(message);
    }
}
