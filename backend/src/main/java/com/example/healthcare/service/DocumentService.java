package com.example.healthcare.service;

import com.example.healthcare.entity.Document;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.repository.DocumentRepository;
import com.example.healthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Document saveDocument(Long patientId, String fileName, String contentType, byte[] data) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Document document = new Document();
        document.setPatient(patient);
        document.setFileName(fileName);
        document.setContentType(contentType);
        document.setData(data);
        return documentRepository.save(document);
    }

    public Document getDocument(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public List<Document> getDocumentsForPatient(Long patientId) {
        return documentRepository.findByPatientId(patientId);
    }
}