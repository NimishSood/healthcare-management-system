package com.example.healthcare.controller;


import com.example.healthcare.entity.Admin;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final SecurityUtils securityUtils;

    @PostMapping("/query/admin")
    public ResponseEntity<String> adminQuery(@RequestBody Map<String, Object> body,
                                             HttpServletRequest request) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        String token = request.getHeader("Authorization");
        String resp = aiService.forwardAdminQuery(body, token);
        Object pidObj = body.get("patient_id");
        Long patientId = pidObj instanceof Number ? ((Number) pidObj).longValue() : null;
        String query = (String) body.getOrDefault("query", "");

        AiService.ParsedAiResponse parsed = aiService.parseResponse(resp);
        String status = parsed.error() ? "ERROR" : (parsed.hasData() ? "FOUND" : "NOT_FOUND");
        aiService.logAdminQuery(admin.getEmail(), patientId, query, parsed.intent(), status);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/query/patient")
    public ResponseEntity<String> patientQuery(@RequestBody Map<String, Object> body,
                                               HttpServletRequest request) {
        securityUtils.getAuthenticatedPatient();
        String token = request.getHeader("Authorization");
        String resp = aiService.forwardPatientQuery(body, token);
        return ResponseEntity.ok(resp);
    }
}