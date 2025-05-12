package com.example.healthcare.service;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service

    public Patient getPatientById(Long id) {
        return patientRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found."));
    }

    @Transactional
    public void updatePatient(Long id, Patient updatedPatient) {
        Patient existing = getPatientById(id);
        String previousData = existing.toString();

        if (updatedPatient.getFirstName() != null) {
            existing.setFirstName(updatedPatient.getFirstName());
        }
        if (updatedPatient.getLastName() != null) {
            existing.setLastName(updatedPatient.getLastName());
        }
        if (updatedPatient.getPhoneNumber() != null) {
            existing.setPhoneNumber(updatedPatient.getPhoneNumber());
        }
        if (updatedPatient.getInsuranceProvider() != null) {
            existing.setInsuranceProvider(updatedPatient.getInsuranceProvider());
        }

        patientRepository.save(existing);

        auditLogService.logAction(
                "Patient Profile Updated", existing.getEmail(), "PATIENT",
                "Patient ID: " + id, previousData, existing.toString()
        );
    }

    @Transactional
    public void softDeletePatient(Long id) {
        Patient patient = getPatientById(id);
        patient.setDeleted(true);
        patientRepository.save(patient);

        auditLogService.logAction(
                "Patient Account Deleted", patient.getEmail(), "PATIENT",
                "Patient ID: " + id, patient.toString(), null
        );
    }

    public List<?> getAppointments(Long patientId) {
        return List.of(); // Placeholder logic
    }

    @Transactional
    public void bookAppointment(Long patientId, Long doctorId) {
        // Logic to book appointment

        auditLogService.logAction(
                "Appointment Booked", "Patient ID: " + patientId, "PATIENT",
                "Doctor ID: " + doctorId, null, "Booked Appointment"
        );
    }

    public List<?> getPrescriptions(Long patientId) {
        return List.of();
    }

    @Transactional
    public void requestRefill(Long patientId, Long prescriptionId) {
        // Request refill logic

        auditLogService.logAction(
                "Prescription Refill Requested", "Patient ID: " + patientId, "PATIENT",
                "Prescription ID: " + prescriptionId, null, "Requested Refill"
        );
    }

    public List<?> getMessages(Long patientId) {
        return List.of();
    }

    @Transactional
    public void sendMessage(Long patientId, Long doctorId, String message) {
        // Send message logic

        auditLogService.logAction(
                "Message Sent", "Patient ID: " + patientId, "PATIENT",
                "Doctor ID: " + doctorId, null, "Message Sent"
        );
    }
}
