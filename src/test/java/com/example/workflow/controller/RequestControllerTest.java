package com.example.workflow.controller;

import com.example.workflow.dto.RequestDetailsDTO;
import com.example.workflow.entity.ApprovalHistory;
import com.example.workflow.entity.RequestEntity;
import com.example.workflow.repository.ApprovalHistoryRepo;
import com.example.workflow.security.JwtUtil;
import com.example.workflow.service.RequestService;
import com.example.workflow.service.WorkflowEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;


@WebMvcTest(RequestController.class)
@EnableMethodSecurity
@Import(RequestControllerTest.TestSecurityBeans.class)
class RequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean RequestService service;
    @MockitoBean WorkflowEngineService engine;
    @MockitoBean
    ApprovalHistoryRepo historyRepo;

    @TestConfiguration
    static class TestSecurityBeans {
        @Bean
        JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "REQUESTER")
    void create_shouldReturnRequest() throws Exception {
        RequestEntity req = new RequestEntity();
        req.setId(1L);
        req.setType("LEAVE");

        when(service.create("LEAVE", "user")).thenReturn(req);

        mockMvc.perform(post("/requests")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"type\":\"LEAVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "manager", roles = "APPROVER")
    void approve_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/requests/1/approve").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Request approved successfully"));

        verify(engine).approve(eq(1L), eq("manager"), eq("ROLE_APPROVER"));
    }

    @Test
    @WithMockUser(username = "manager", roles = "APPROVER")
    void reject_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/requests/1/reject").with(csrf()))
                .andExpect(status().isOk());

        verify(engine).reject(1L, "manager");
    }

    @Test
    @WithMockUser(username = "user", roles = "REQUESTER")
    void getRequest_shouldReturnDetails() throws Exception {
        RequestDetailsDTO dto = new RequestDetailsDTO();
        dto.setId(1L);
        dto.setType("LEAVE");

        when(service.getRequestDetails(1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = "REQUESTER")
    void history_shouldReturnList() throws Exception {
        ApprovalHistory h = new ApprovalHistory();
        h.setAction("APPROVED");

        when(historyRepo.findByRequestId(1L)).thenReturn(List.of(h));

        mockMvc.perform(get("/requests/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("APPROVED"));
    }
}
