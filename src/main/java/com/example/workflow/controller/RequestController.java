package com.example.workflow.controller;

import com.example.workflow.dto.RequestDetailsDTO;
import com.example.workflow.dto.SuccessResponse;
import com.example.workflow.entity.ApprovalHistory;
import com.example.workflow.entity.RequestEntity;
import com.example.workflow.repository.ApprovalHistoryRepo;
import com.example.workflow.service.RequestService;
import com.example.workflow.service.WorkflowEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService service;
    private final WorkflowEngineService engine;
    private final ApprovalHistoryRepo historyRepo;
    @PostMapping
    @PreAuthorize("hasRole('REQUESTER')")
    public RequestEntity create(@RequestBody Map<String,String> body,
                                Authentication auth){
        return service.create(body.get("type"), auth.getName());
    }


    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('APPROVER','ADMIN')")
    public ResponseEntity<SuccessResponse> approve(@PathVariable Long id, Authentication auth){
        engine.approve(id,auth.getName(),
                auth.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok(new SuccessResponse("Request approved successfully"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('APPROVER')")
    public ResponseEntity<SuccessResponse> reject(@PathVariable Long id,Authentication auth){
        engine.reject(id,auth.getName());
        return ResponseEntity.ok(new SuccessResponse("Request rejected successfully"));
    }




    @GetMapping("/history/{id}")
    public List<ApprovalHistory> history(@PathVariable Long id){
        return historyRepo.findByRequestId(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('REQUESTER','APPROVER','ADMIN')")
    public RequestDetailsDTO getRequest(@PathVariable Long id){
        return service.getRequestDetails(id);
    }

}

