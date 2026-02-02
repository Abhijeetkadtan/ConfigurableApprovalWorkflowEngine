package com.example.workflow.repository;

import com.example.workflow.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepo extends JpaRepository<ApprovalHistory,Long> {
    List<ApprovalHistory> findByRequestId(Long id);
}
