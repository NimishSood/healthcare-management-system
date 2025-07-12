package com.example.healthcare.service;

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

    public AiService(RestTemplate restTemplate,
                     @Value("${ai.service.url}") String aiBaseUrl) {
        this.restTemplate = restTemplate;
        this.aiBaseUrl = aiBaseUrl;
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
}