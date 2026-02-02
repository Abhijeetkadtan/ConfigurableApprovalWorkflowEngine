package com.example.workflow.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String user;
    private String pass;
    private String role;
}
