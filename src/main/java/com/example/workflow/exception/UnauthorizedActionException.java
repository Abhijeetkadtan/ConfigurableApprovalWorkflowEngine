package com.example.workflow.exception;

public class UnauthorizedActionException extends WorkflowException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
