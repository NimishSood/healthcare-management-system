package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.healthcare.entity.Attachment;
import com.example.healthcare.entity.enums.AttachmentParentType;
import com.example.healthcare.service.AttachmentService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AttachmentService attachmentService;

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
        try {
            attachmentService.deleteAllForParent(AttachmentParentType.MEDICAL_RECORD, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public Attachment addAttachment(Long recordId, MultipartFile file) throws Exception {
        getMedicalRecord(recordId);
        return attachmentService.addAttachment(AttachmentParentType.MEDICAL_RECORD, recordId, file);
    }

    public List<Attachment> getAttachments(Long recordId) {
        return attachmentService.listAttachments(AttachmentParentType.MEDICAL_RECORD, recordId);
    }

    public Attachment getAttachment(Long attachmentId) {
        return attachmentService.getAttachment(attachmentId);
    }

    public void deleteAttachment(Long attachmentId) throws Exception {
        attachmentService.deleteAttachment(attachmentId);
    }
}