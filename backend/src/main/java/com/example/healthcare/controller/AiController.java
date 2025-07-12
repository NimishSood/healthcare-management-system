package com.example.healthcare.controller;

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
        securityUtils.getAuthenticatedAdmin();
        String token = request.getHeader("Authorization");
        String resp = aiService.forwardAdminQuery(body, token);
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