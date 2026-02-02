package com.example.workflow.service;

import com.example.workflow.entity.ApprovalHistory;
import com.example.workflow.entity.ApprovalStep;
import com.example.workflow.entity.RequestEntity;
import com.example.workflow.exception.InvalidStateException;
import com.example.workflow.exception.SelfApprovalException;
import com.example.workflow.exception.UnauthorizedActionException;
import com.example.workflow.repository.ApprovalHistoryRepo;
import com.example.workflow.repository.ApprovalStepRepo;
import com.example.workflow.repository.RequestRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService{

    private final RequestRepo requestRepo;
    private final ApprovalStepRepo stepRepo;
    private final ApprovalHistoryRepo historyRepo;




    @Transactional
    public void approve(Long id, String user, String role){

        RequestEntity req = requestRepo.findById(id).orElseThrow();

        validateStateForAction(req, "APPROVE");

        if (req.getCreatedBy().equals(user))
            throw new SelfApprovalException("Requester cannot approve their own request");

        if(role.equals("ROLE_ADMIN")){
            req.setStatus("APPROVED");
            log(id,"ADMIN_OVERRIDE",user);
            return;
        }

        ApprovalStep step = stepRepo
                .findByRequestTypeAndStepOrder(req.getType(), req.getCurrentStep())
                .orElseThrow();

        if (!step.getRole().equals(role))
            throw new UnauthorizedActionException("You are not authorized for this approval step");


        log(id,"APPROVED_STEP_"+req.getCurrentStep(),user);

        Optional<ApprovalStep> next =
                stepRepo.findByRequestTypeAndStepOrder(req.getType(), req.getCurrentStep()+1);

        if(next.isPresent()){
            req.setCurrentStep(req.getCurrentStep()+1);
            req.setStatus("IN_PROGRESS");
        }else{
            req.setStatus("APPROVED");
        }
    }


    @Transactional
    public void reject(Long id, String user){

        RequestEntity req = requestRepo.findById(id).orElseThrow();

        validateStateForAction(req, "REJECT");

        req.setStatus("REJECTED");
        log(id,"REJECTED",user);
    }


    private void log(Long id,String action,String user){
        ApprovalHistory h=new ApprovalHistory();
        h.setRequestId(id);
        h.setAction(action);
        h.setActionBy(user);
        h.setActionAt(LocalDateTime.now());
        historyRepo.save(h);
    }

    private void validateStateForAction(RequestEntity req, String action){

        if("APPROVED".equals(req.getStatus()) && "APPROVE".equals(action))
            throw new InvalidStateException("Request already approved.");

        if("REJECTED".equals(req.getStatus()) && "APPROVE".equals(action))
            throw new InvalidStateException("Cannot approve a rejected request.");

        if("APPROVED".equals(req.getStatus()) && "REJECT".equals(action))
            throw new InvalidStateException("Cannot reject an approved request.");
    }

}

