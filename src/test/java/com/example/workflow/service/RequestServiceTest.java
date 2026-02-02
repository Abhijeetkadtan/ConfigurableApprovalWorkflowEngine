package com.example.workflow.service;

import com.example.workflow.dto.RequestDetailsDTO;
import com.example.workflow.entity.*;
import com.example.workflow.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    @InjectMocks
    private RequestService service;

    @Mock
    private RequestRepo requestRepo;
    @Mock
    private ApprovalStepRepo stepRepo;

    private ApprovalStep step;
    private RequestEntity request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        step = new ApprovalStep();
        step.setRequestType("LEAVE");
        step.setStepOrder(1);
        step.setRole("ROLE_APPROVER");

        request = new RequestEntity();
        request.setId(1L);
        request.setType("LEAVE");
        request.setStatus("IN_PROGRESS");
        request.setCurrentStep(1);
        request.setCreatedBy("ram");
    }

    @Test
    void create_shouldInitializeWorkflow(){

        when(stepRepo.findByRequestTypeOrderByStepOrder("LEAVE"))
                .thenReturn(List.of(step));

        when(requestRepo.save(any(RequestEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        RequestEntity result = service.create("LEAVE","ram");

        assertEquals("LEAVE", result.getType());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals(1, result.getCurrentStep());
        assertEquals("ram", result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
    }
    @Test
    void create_shouldThrowIfNoStepsConfigured(){

        when(stepRepo.findByRequestTypeOrderByStepOrder("LEAVE"))
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class,
                () -> service.create("LEAVE","ram"));
    }
    @Test
    void getRequestDetails_shouldReturnDTO(){

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",1))
                .thenReturn(Optional.of(step));

        RequestDetailsDTO dto = service.getRequestDetails(1L);

        assertEquals(1L, dto.getId());
        assertEquals("LEAVE", dto.getType());
        assertEquals("IN_PROGRESS", dto.getStatus());
        assertEquals(1, dto.getCurrentStep());
        assertEquals("ROLE_APPROVER", dto.getNextApproverRole());
    }
    @Test
    void getRequestDetails_shouldWorkWithoutNextStep(){

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepo.findByRequestTypeAndStepOrder("LEAVE",1))
                .thenReturn(Optional.empty());

        RequestDetailsDTO dto = service.getRequestDetails(1L);

        assertNull(dto.getNextApproverRole());
    }

}