package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.repository.*;
import com.example.healthcare.repository.LabResultAttachmentRepository;
import com.example.healthcare.entity.LabResultAttachment;
import com.example.healthcare.storage.StorageService;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final LabResultAttachmentRepository labResultAttachmentRepository;
    private final StorageService storageService;

    @Transactional
    public LabResult createLabResult(Long patientId, Long doctorId, Long appointmentId,
                                     String testName, String resultValue, String units,
                                     String normalRange, String notes) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        Appointment appointment = null;
        if (appointmentId != null) {
            appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        }
        LabResult lr = new LabResult();
        lr.setPatient(patient);
        lr.setDoctor(doctor);
        lr.setAppointment(appointment);
        lr.setTestName(testName);
        lr.setResultValue(resultValue);
        lr.setUnits(units);
        lr.setNormalRange(normalRange);
        lr.setNotes(notes);
        return labResultRepository.save(lr);
    }

    public LabResult getLabResult(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lab result not found"));
    }

    public List<LabResult> getLabResultsForPatient(Long patientId) {
        return labResultRepository.findByPatientId(patientId);
    }

    @Transactional
    public LabResult updateLabResult(Long id, LabResult data) {
        LabResult lr = getLabResult(id);
        lr.setTestName(data.getTestName());
        lr.setResultValue(data.getResultValue());
        lr.setUnits(data.getUnits());
        lr.setNormalRange(data.getNormalRange());
        lr.setNotes(data.getNotes());
        return labResultRepository.save(lr);
    }

    public void deleteLabResult(Long id) {
        labResultRepository.deleteById(id);
    }
    @Transactional
    public LabResultAttachment addAttachment(Long labResultId, MultipartFile file) throws Exception {
        LabResult lr = getLabResult(labResultId);
        String key = storageService.store(file);
        LabResultAttachment att = new LabResultAttachment();
        att.setLabResult(lr);
        att.setFileName(file.getOriginalFilename());
        att.setContentType(file.getContentType());
        att.setStorageKey(key);
        att.setFileSize(file.getSize());
        return labResultAttachmentRepository.save(att);
    }

    public List<LabResultAttachment> getAttachments(Long labResultId) {
        return labResultAttachmentRepository.findByLabResultId(labResultId);
    }

    public LabResultAttachment getAttachment(Long attachmentId) {
        return labResultAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
    }

    public void deleteAttachment(Long attachmentId) {
        labResultAttachmentRepository.deleteById(attachmentId);
    }
}