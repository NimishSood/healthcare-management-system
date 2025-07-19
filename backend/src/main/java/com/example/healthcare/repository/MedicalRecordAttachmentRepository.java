package com.example.healthcare.repository;

import com.example.healthcare.entity.MedicalRecordAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordAttachmentRepository extends JpaRepository<MedicalRecordAttachment, Long> {
    List<MedicalRecordAttachment> findByMedicalRecordId(Long medicalRecordId);
}