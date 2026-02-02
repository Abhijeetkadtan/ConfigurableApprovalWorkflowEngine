package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="approval_history")
@Data
public class ApprovalHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long requestId;
    private String action;
    private String actionBy;
    private LocalDateTime actionAt;
}
