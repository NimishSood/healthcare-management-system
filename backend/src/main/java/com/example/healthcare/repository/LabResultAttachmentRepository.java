package com.example.healthcare.repository;

import com.example.healthcare.entity.LabResultAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabResultAttachmentRepository extends JpaRepository<LabResultAttachment, Long> {
    List<LabResultAttachment> findByLabResultId(Long labResultId);
}