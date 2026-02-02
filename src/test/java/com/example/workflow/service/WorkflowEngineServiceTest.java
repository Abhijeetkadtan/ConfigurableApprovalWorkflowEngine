package com.example.workflow.service;

import com.example.workflow.entity.*;
import com.example.workflow.repository.*;
import com.example.workflow.exception.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkflowEngineServiceTest {

    @InjectMocks
    private WorkflowEngineService engine;

    @Mock
    private RequestRepo requestRepo;
    @Mock
    private ApprovalStepRepo stepRepo;
    @Mock
    private ApprovalHistoryRepo historyRepo;

    private RequestEntity request;
    private ApprovalStep step;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        request = new RequestEntity();
        request.setId(1L);
        request.setType("LEAVE");
        request.setStatus("IN_PROGRESS");
        request.setCurrentStep(1);
        request.setCreatedBy("ram");

        step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("ROLE_APPROVER");
    }
    @Test
    void approve_shouldMoveToNextStep() {

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",1)).thenReturn(Optional.of(step));

        ApprovalStep nextStep = new ApprovalStep();
        nextStep.setStepOrder(2);
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",2))
                .thenReturn(Optional.of(nextStep));

        engine.approve(1L,"manager","ROLE_APPROVER");

        assertEquals(2, request.getCurrentStep());
        assertEquals("IN_PROGRESS", request.getStatus());
    }

    @Test
    void approve_shouldCompleteWorkflow() {

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",1)).thenReturn(Optional.of(step));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",2))
                .thenReturn(Optional.empty());

        engine.approve(1L,"manager","ROLE_APPROVER");

        assertEquals("APPROVED", request.getStatus());
    }

    @Test
    void approve_shouldFailForSelfApproval(){

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(SelfApprovalException.class,
                () -> engine.approve(1L,"ram","ROLE_APPROVER"));
    }
    @Test
    void approve_shouldFailWhenAlreadyApproved(){

        request.setStatus("APPROVED");
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(InvalidStateException.class,
                () -> engine.approve(1L,"manager","ROLE_APPROVER"));
    }
    @Test
    void approve_shouldFailForWrongRole(){

        step.setRole("ROLE_ADMIN");

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",1)).thenReturn(Optional.of(step));

        assertThrows(UnauthorizedActionException.class,
                () -> engine.approve(1L,"manager","ROLE_APPROVER"));
    }
    @Test
    void reject_shouldUpdateStatus(){

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));

        engine.reject(1L,"manager");

        assertEquals("REJECTED", request.getStatus());
    }



}