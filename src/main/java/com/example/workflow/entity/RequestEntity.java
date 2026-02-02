package com.example.workflow.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="requests")
@Data
public class RequestEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String status;
    private Integer currentStep;
    private String createdBy;
    private LocalDateTime createdAt;
}
