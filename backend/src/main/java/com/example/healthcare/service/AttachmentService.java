package com.example.healthcare.service;

import com.example.healthcare.entity.Attachment;
import com.example.healthcare.entity.enums.AttachmentParentType;
import com.example.healthcare.repository.AttachmentRepository;
import com.example.healthcare.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

    @Transactional
    public Attachment addAttachment(AttachmentParentType parentType, Long parentId, MultipartFile file) throws Exception {
        String key = storageService.store(file);
        Attachment att = new Attachment();
        att.setParentType(parentType);
        att.setParentId(parentId);
        att.setFileName(file.getOriginalFilename());
        att.setContentType(file.getContentType());
        att.setStorageKey(key);
        att.setFileSize(file.getSize());
        return attachmentRepository.save(att);
    }

    public List<Attachment> listAttachments(AttachmentParentType parentType, Long parentId) {
        return attachmentRepository.findByParentTypeAndParentId(parentType, parentId);
    }

    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
    }

    @Transactional
    public void deleteAttachment(Long id) throws Exception {
        Attachment att = getAttachment(id);
        attachmentRepository.delete(att);
        storageService.delete(att.getStorageKey());
    }

    @Transactional
    public void deleteAllForParent(AttachmentParentType parentType, Long parentId) throws Exception {
        List<Attachment> list = attachmentRepository.findByParentTypeAndParentId(parentType, parentId);
        for (Attachment att : list) {
            attachmentRepository.delete(att);
            storageService.delete(att.getStorageKey());
        }
    }
}