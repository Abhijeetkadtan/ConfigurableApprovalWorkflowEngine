package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="approval_steps")
@Data
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String requestType;
    private Integer stepOrder;
    private String role;
}
