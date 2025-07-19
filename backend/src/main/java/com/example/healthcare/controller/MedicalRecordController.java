package com.example.healthcare.controller;

import com.example.healthcare.entity.*;
import com.example.healthcare.entity.Attachment;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.User;
import com.example.healthcare.service.MedicalRecordService;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.storage.StorageService;
import com.example.healthcare.exception.UnauthorizedAccessException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final SecurityUtils securityUtils;
    private final StorageService storageService;

    private void verifyAccess(MedicalRecord record) {
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Patient p) {
            if (!record.getPatient().getId().equals(p.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (user instanceof Doctor d) {
            if (record.getDoctor() == null || !record.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (user instanceof Admin) {
            // admins allowed
        } else {
            throw new UnauthorizedAccessException("Access denied");
        }
    }
    private void verifyModifyAccess(MedicalRecord record) {
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Doctor d) {
            if (record.getDoctor() == null || !record.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (!(user instanceof Admin)) {
            throw new UnauthorizedAccessException("Access denied");
        }
    }


    @PostMapping
    public MedicalRecord create(@RequestBody MedicalRecord request) {
        // only doctors or admins can create
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Doctor d) {
            if (request.getDoctor() != null && !request.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (!(user instanceof Admin)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        return medicalRecordService.createMedicalRecord(
                request.getPatient().getId(),
                request.getDoctor() != null ? request.getDoctor().getId() : null,
                request.getAppointment() != null ? request.getAppointment().getId() : null,
                request.getDescription()
        );
    }

    @GetMapping("/{id}")
    public MedicalRecord get(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecord(id);
        verifyAccess(record);
        return record;
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecord> forPatient(@PathVariable Long patientId) {
        return medicalRecordService.getRecordsForPatient(patientId);
    }

    @PutMapping("/{id}")
    public MedicalRecord update(@PathVariable Long id, @RequestBody MedicalRecord request) {
        MedicalRecord record = medicalRecordService.getMedicalRecord(id);
        verifyModifyAccess(record);
        return medicalRecordService.updateMedicalRecord(id, request.getDescription());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecord(id);
        verifyModifyAccess(record);
        medicalRecordService.deleteMedicalRecord(id);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping("/{id}/attachments")
    public List<Attachment> upload(@PathVariable Long id,
                                   @RequestParam("files") MultipartFile[] files) throws Exception {
        MedicalRecord record = medicalRecordService.getMedicalRecord(id);
        verifyModifyAccess(record);
        List<Attachment> list = new java.util.ArrayList<>();
        for (MultipartFile f : files) {
            list.add(medicalRecordService.addAttachment(id, f));
        }
        return list;
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','PATIENT')")
    @GetMapping("/{id}/attachments")
    public List<Attachment> listAttachments(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecord(id);
        verifyAccess(record);
        return medicalRecordService.getAttachments(id);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','PATIENT')")
    @GetMapping("/attachments/{attId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attId) throws Exception {
        Attachment att = medicalRecordService.getAttachment(attId);
        if (att.getParentType() != com.example.healthcare.entity.enums.AttachmentParentType.MEDICAL_RECORD) {
            throw new UnauthorizedAccessException("Invalid attachment");
        }
        MedicalRecord record = medicalRecordService.getMedicalRecord(att.getParentId());
        verifyAccess(record);
        Resource res = storageService.loadAsResource(att.getStorageKey());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(att.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + att.getFileName() + "\"")
                .body(res);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @DeleteMapping("/attachments/{attId}")
    public void deleteAttachment(@PathVariable Long attId) throws Exception {
        Attachment att = medicalRecordService.getAttachment(attId);
        if (att.getParentType() != com.example.healthcare.entity.enums.AttachmentParentType.MEDICAL_RECORD) {
            throw new UnauthorizedAccessException("Invalid attachment");
        }
        MedicalRecord record = medicalRecordService.getMedicalRecord(att.getParentId());
        verifyModifyAccess(record);
        medicalRecordService.deleteAttachment(attId);
    }
}