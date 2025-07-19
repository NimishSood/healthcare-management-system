package com.example.healthcare.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate;
    private final String aiBaseUrl;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(RestTemplate restTemplate,
                     @Value("${ai.service.url}") String aiBaseUrl,
                     AuditLogService auditLogService) {
        this.restTemplate = restTemplate;
        this.aiBaseUrl = aiBaseUrl;
        this.auditLogService = auditLogService;
    }

    public String forwardAdminQuery(Map<String, Object> body, String authHeader) {
        return postToAi("/query/admin", body, authHeader);
    }

    public String forwardPatientQuery(Map<String, Object> body, String authHeader) {
        return postToAi("/query/patient", body, authHeader);
    }

    private String postToAi(String path, Map<String, Object> body, String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set("Authorization", authHeader);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(aiBaseUrl + path, entity, String.class);
        return response.getBody();
    }
    /**
     * Logs an admin AI query using the standard audit log format.
     */
    public void logAdminQuery(String adminEmail, Long patientId, String queryText,
                              String intent, String status) {
        String affected = "Patient ID: " + patientId;
        String previous = "Query: " + queryText;
        String updated = "Intent: " + intent + "; Status: " + status;
        auditLogService.logAction("AI Admin Query", adminEmail, "ADMIN",
                affected, previous, updated);
    }

    /**
     * Utility to extract intent and success info from AI service response.
     */
    public ParsedAiResponse parseResponse(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>(){});
            String intent = map.getOrDefault("intent", "").toString();
            boolean error = map.containsKey("error");
            boolean hasData = map.get("data") != null && !map.get("data").toString().isBlank();
            return new ParsedAiResponse(intent, hasData, error, map.get("data"));
        } catch (Exception e) {
            return new ParsedAiResponse("", false, true, null);
        }
    }

    public record ParsedAiResponse(String intent, boolean hasData, boolean error, Object data) {}
}