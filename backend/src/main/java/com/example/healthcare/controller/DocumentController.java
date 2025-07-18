package com.example.healthcare.controller;

import com.example.healthcare.entity.Document;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/upload")
    public Document upload(@RequestParam("file") MultipartFile file) throws Exception {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return documentService.saveDocument(
                patient.getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
    }

    @GetMapping("/mine")
    public List<Document> myDocuments() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return documentService.getDocumentsForPatient(patient.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) {
        Document doc = documentService.getDocument(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(new ByteArrayResource(doc.getData()));
    }
}