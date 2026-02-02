package com.example.workflow.repository;

import com.example.workflow.entity.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepo extends JpaRepository<ApprovalStep,Long> {
    List<ApprovalStep> findByRequestTypeOrderByStepOrder(String type);
    Optional<ApprovalStep> findByRequestTypeAndStepOrder(String type, Integer step);
}