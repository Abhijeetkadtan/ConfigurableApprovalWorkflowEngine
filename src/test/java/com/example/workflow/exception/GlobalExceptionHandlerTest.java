package com.example.workflow.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleWorkflow_shouldReturnBadRequestAndMessage() {

        WorkflowException ex = new WorkflowException("Invalid state transition");

        ResponseEntity<?> response = handler.handleWorkflow(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Invalid state transition", error.getMessage());
    }
}
