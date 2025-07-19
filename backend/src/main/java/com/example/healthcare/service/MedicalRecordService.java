package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.healthcare.repository.MedicalRecordAttachmentRepository;
import com.example.healthcare.entity.MedicalRecordAttachment;
import com.example.healthcare.storage.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordAttachmentRepository medicalRecordAttachmentRepository;
    private final StorageService storageService;

    @Transactional
    public MedicalRecord createMedicalRecord(Long patientId, Long doctorId, Long appointmentId,
                                             String description) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = null;
        if (doctorId != null) {
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        }
        Appointment appointment = null;
        if (appointmentId != null) {
            appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        }
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setAppointment(appointment);
        record.setDescription(description);
        return medicalRecordRepository.save(record);
    }

    public MedicalRecord getMedicalRecord(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
    }

    public List<MedicalRecord> getRecordsForPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long id, String description) {
        MedicalRecord record = getMedicalRecord(id);
        record.setDescription(description);
        return medicalRecordRepository.save(record);
    }

    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }
    @Transactional
    public MedicalRecordAttachment addAttachment(Long recordId, MultipartFile file) throws Exception {
        MedicalRecord record = getMedicalRecord(recordId);
        String key = storageService.store(file);
        MedicalRecordAttachment att = new MedicalRecordAttachment();
        att.setMedicalRecord(record);
        att.setFileName(file.getOriginalFilename());
        att.setContentType(file.getContentType());
        att.setStorageKey(key);
        att.setFileSize(file.getSize());
        return medicalRecordAttachmentRepository.save(att);
    }

    public List<MedicalRecordAttachment> getAttachments(Long recordId) {
        return medicalRecordAttachmentRepository.findByMedicalRecordId(recordId);
    }

    public MedicalRecordAttachment getAttachment(Long attachmentId) {
        return medicalRecordAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
    }

    public void deleteAttachment(Long attachmentId) {
        medicalRecordAttachmentRepository.deleteById(attachmentId);
    }
}