package com.example.healthcare.repository;

import com.example.healthcare.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPatientId(Long patientId);
}