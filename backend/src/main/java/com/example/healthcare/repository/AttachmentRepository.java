package com.example.healthcare.repository;

import com.example.healthcare.entity.Attachment;
import com.example.healthcare.entity.enums.AttachmentParentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByParentTypeAndParentId(AttachmentParentType parentType, Long parentId);
}