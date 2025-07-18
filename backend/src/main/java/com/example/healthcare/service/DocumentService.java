package com.example.healthcare.service;

import com.example.healthcare.entity.Document;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.repository.DocumentRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final StorageService storageService;

    @Transactional
    public Document saveDocument(Long patientId, MultipartFile file) throws Exception {
        Patient patient = patientRepository.getReferenceById(patientId);

        String key = storageService.store(file);
        Document document = new Document();
        document.setPatient(patient);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setStorageKey(key);
        document.setFileSize(file.getSize());
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