package com.example.healthcare.service;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service
    private final PasswordEncoder passwordEncoder;
    private final com.example.healthcare.repository.prescriptionRepository prescriptionRepository;

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

    /**
     * Change the authenticated patient's password.
     *
     * @param id           the patient’s ID
     * @param oldPassword  the current (plain-text) password
     * @param newPassword  the new (plain-text) password
     * @throws IllegalArgumentException if the old password does not match
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Patient patient = getPatientById(id);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, patient.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Audit previous hash (optional—be mindful of GDPR/security)
        String previousHash = patient.getPassword();

        // Encode and set new password
        patient.setPassword(passwordEncoder.encode(newPassword));
        patientRepository.save(patient);

        // Log the change
        auditLogService.logAction(
                "Patient Password Changed",
                patient.getEmail(),
                "PATIENT",
                "Patient ID: " + id,
                previousHash,
                patient.getPassword()
        );
    }

    public long countPendingRefills(Long patientId) {
        // attempt to fetch prescriptions (may be empty or not yet implemented)
        List<?> prescriptions;
        try {
            prescriptions = getPrescriptions(patientId);
        } catch (Exception e) {
            // if getPrescriptions is not implemented or fails, swallow and return 0
            return 0L;
        }
        if (prescriptions == null) {
            return 0L;
        }
        // stub: assume none are pending until you add real criteria
        return 0L;
    }
}
