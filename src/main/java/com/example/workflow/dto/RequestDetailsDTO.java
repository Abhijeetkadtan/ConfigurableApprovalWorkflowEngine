package com.example.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestDetailsDTO {
    private Long id;
    private String type;
    private String status;
    private Integer currentStep;
    private String nextApproverRole;
    private String createdBy;
    private LocalDateTime createdAt;
}
