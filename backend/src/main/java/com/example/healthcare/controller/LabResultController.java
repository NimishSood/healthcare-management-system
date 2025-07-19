package com.example.healthcare.controller;

import com.example.healthcare.entity.*;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.User;
import com.example.healthcare.service.LabResultService;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.storage.StorageService;
import com.example.healthcare.exception.UnauthorizedAccessException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lab-results")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;
    private final SecurityUtils securityUtils;
    private final StorageService storageService;

    private void verifyAccess(LabResult lr) {
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Patient p) {
            if (!lr.getPatient().getId().equals(p.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (user instanceof Doctor d) {
            if (!lr.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (user instanceof Admin) {
            // admins always allowed

        } else {
            throw new UnauthorizedAccessException("Access denied");
        }
    }
    private void verifyModifyAccess(LabResult lr) {
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Doctor d) {
            if (!lr.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (!(user instanceof Admin)) {
            throw new UnauthorizedAccessException("Access denied");
        }
    }

    @PostMapping
    public LabResult create(@RequestBody LabResult request) {
        // only doctors or admins can create
        User user = securityUtils.getAuthenticatedUser();
        if (user instanceof Doctor d) {
            if (!request.getDoctor().getId().equals(d.getId())) {
                throw new UnauthorizedAccessException("Access denied");
            }
        } else if (!(user instanceof Admin)) {
            throw new UnauthorizedAccessException("Access denied");
        }
        return labResultService.createLabResult(
                request.getPatient().getId(),
                request.getDoctor().getId(),
                request.getAppointment() != null ? request.getAppointment().getId() : null,
                request.getTestName(),
                request.getResultValue(),
                request.getUnits(),
                request.getNormalRange(),
                request.getNotes()
        );
    }

    @GetMapping("/{id}")
    public LabResult get(@PathVariable Long id) {
        LabResult lr = labResultService.getLabResult(id);
        verifyAccess(lr);
        return lr;
    }

    @GetMapping("/patient/{patientId}")
    public List<LabResult> forPatient(@PathVariable Long patientId) {
        return labResultService.getLabResultsForPatient(patientId);
    }

    @PutMapping("/{id}")
    public LabResult update(@PathVariable Long id, @RequestBody LabResult request) {
        LabResult lr = labResultService.getLabResult(id);
        verifyModifyAccess(lr);
        return labResultService.updateLabResult(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        LabResult lr = labResultService.getLabResult(id);
        verifyModifyAccess(lr);
        labResultService.deleteLabResult(id);
    }
    @PostMapping("/{id}/attachments")
    public List<LabResultAttachment> upload(@PathVariable Long id,
                                            @RequestParam("files") MultipartFile[] files) throws Exception {
        LabResult lr = labResultService.getLabResult(id);
        verifyModifyAccess(lr);
        List<LabResultAttachment> list = new java.util.ArrayList<>();
        for (MultipartFile f : files) {
            list.add(labResultService.addAttachment(id, f));
        }
        return list;
    }

    @GetMapping("/{id}/attachments")
    public List<LabResultAttachment> listAttachments(@PathVariable Long id) {
        LabResult lr = labResultService.getLabResult(id);
        verifyAccess(lr);
        return labResultService.getAttachments(id);
    }

    @GetMapping("/attachments/{attId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attId) throws Exception {
        LabResultAttachment att = labResultService.getAttachment(attId);
        verifyAccess(att.getLabResult());
        Resource res = storageService.loadAsResource(att.getStorageKey());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(att.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + att.getFileName() + "\"")
                .body(res);
    }

    @DeleteMapping("/attachments/{attId}")
    public void deleteAttachment(@PathVariable Long attId) {
        LabResultAttachment att = labResultService.getAttachment(attId);
        verifyModifyAccess(att.getLabResult());
        labResultService.deleteAttachment(attId);
    }
}