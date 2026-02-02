package com.example.workflow.service;

import com.example.workflow.dto.RequestDetailsDTO;
import com.example.workflow.entity.ApprovalStep;
import com.example.workflow.entity.RequestEntity;
import com.example.workflow.repository.ApprovalStepRepo;
import com.example.workflow.repository.RequestRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepo requestRepo;
    private final ApprovalStepRepo stepRepo;

    @Transactional
    public RequestEntity create(String type, String user){

        ApprovalStep first=stepRepo.findByRequestTypeOrderByStepOrder(type)
                .stream().findFirst().orElseThrow();

        RequestEntity req=new RequestEntity();
        req.setType(type);
        req.setStatus("IN_PROGRESS");
        req.setCurrentStep(first.getStepOrder());
        req.setCreatedBy(user);
        req.setCreatedAt(LocalDateTime.now());

        return requestRepo.save(req);
    }

    public RequestDetailsDTO getRequestDetails(Long id){

        RequestEntity req = requestRepo.findById(id).orElseThrow();

        RequestDetailsDTO dto = new RequestDetailsDTO();
        dto.setId(req.getId());
        dto.setType(req.getType());
        dto.setStatus(req.getStatus());
        dto.setCurrentStep(req.getCurrentStep());
        dto.setCreatedBy(req.getCreatedBy());
        dto.setCreatedAt(req.getCreatedAt());

        stepRepo.findByRequestTypeAndStepOrder(req.getType(), req.getCurrentStep())
                .ifPresent(step -> dto.setNextApproverRole(step.getRole()));

        return dto;
    }

}


