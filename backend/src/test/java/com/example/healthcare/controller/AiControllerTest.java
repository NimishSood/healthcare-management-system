package com.example.healthcare.controller;

import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiService aiService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AiController aiController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();
    }

    @Test
    void testAdminQuery() throws Exception {
        when(aiService.forwardAdminQuery(anyMap(), eq("Bearer token"))).thenReturn("{\"ok\":true}");

        mockMvc.perform(post("/api/ai/query/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content("{\"patient_id\":1,\"query\":\"q\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ok")));

        verify(securityUtils).getAuthenticatedAdmin();
        verify(aiService).forwardAdminQuery(anyMap(), eq("Bearer token"));
    }

    @Test
    void testPatientQuery() throws Exception {
        when(aiService.forwardPatientQuery(anyMap(), eq("Bearer t"))).thenReturn("{\"ok\":true}");

        mockMvc.perform(post("/api/ai/query/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer t")
                        .content("{\"query\":\"hi\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ok")));

        verify(securityUtils).getAuthenticatedPatient();
        verify(aiService).forwardPatientQuery(anyMap(), eq("Bearer t"));
    }
}