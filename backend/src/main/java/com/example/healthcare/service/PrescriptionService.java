package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditLogService auditLogService;

    /**
     * Creates a new prescription for a given patient by the given doctor.
     * Only doctors can call this, and the doctor must be authorized to treat the patient.
     */
    @Transactional
    public Prescription createPrescription(Doctor prescribingDoctor, Long patientId,
                                           Long appointmentId, String medicationName,
                                           String dosage, String instructions, int refillsLeft) {
        // 1. Validate that the patient exists and is active
        Patient patient = patientRepository.findByIdAndIsDeletedFalse(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found or inactive."));

        // 2. If an appointment is provided, verify it matches the doctor and patient
        Appointment appointment = null;
        if (appointmentId != null) {
            appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
            // Ensure the appointment involves the same doctor and patient
            if (!appointment.getDoctor().getId().equals(prescribingDoctor.getId())
                    || !appointment.getPatient().getId().equals(patientId)) {
                throw new SecurityException("Appointment does not match the doctor and patient.");
            }
        } else {
            // 3. If no appointment, ensure the doctor has a prior relationship with the patient
            boolean hasRelationship = appointmentRepository
                    .existsByDoctorIdAndPatientIdAndIsDeletedFalse(prescribingDoctor.getId(), patientId);
            if (!hasRelationship) {
                throw new SecurityException("Doctor is not authorized to prescribe for this patient.");
            }
        }

        // 4. Create and save the Prescription entity
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(prescribingDoctor);
        prescription.setAppointment(appointment);
        prescription.setMedicationName(medicationName);
        prescription.setDosage(dosage);
        prescription.setInstructions(instructions);
        prescription.setRefillsLeft(refillsLeft);
        prescription.setDateIssued(LocalDate.now());
        prescription.setDeleted(false);
        Prescription saved = prescriptionRepository.save(prescription);

        // 5. Audit Logging for creation
        String detail = String.format("Medication: %s, Dosage: %s, Refills: %d",
                medicationName, dosage, refillsLeft);
        auditLogService.logAction(
                "Prescription Created",
                prescribingDoctor.getEmail(),                  // performedBy (doctor's email)
                prescribingDoctor.getRole().name(),           // role (e.g., "DOCTOR")
                "Prescription ID: " + saved.getId(),
                null,                                         // no previous data (new record)
                detail                                        // new data summary
        );
        return saved;
    }

    /**
     * Retrieves a prescription by ID, enforcing that the requesting user (doctor/patient/admin)
     * has access to it. Returns the prescription if authorized.
     */
    public Prescription getPrescriptionById(Long prescriptionId, User currentUser) {
        Prescription prescription = prescriptionRepository.findByIdAndIsDeletedFalse(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found."));

        UserRole role = currentUser.getRole();
        if (role == UserRole.PATIENT) {
            // Patients can only view their own prescription
            if (!prescription.getPatient().getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied: not the owner of this prescription.");
            }
        } else if (role == UserRole.DOCTOR) {
            Doctor doctor = (Doctor) currentUser;
            Long docId = doctor.getId();
            // Doctors can view if they issued it or the prescription's patient is under their care
            Long prescriberId = prescription.getDoctor().getId();
            Long patientId = prescription.getPatient().getId();
            boolean isPrescriber = prescriberId.equals(docId);
            boolean isPatientOfDoctor = appointmentRepository
                    .existsByDoctorIdAndPatientIdAndIsDeletedFalse(docId, patientId);
            if (!(isPrescriber || isPatientOfDoctor)) {
                throw new SecurityException("Access denied: not authorized for this patient's prescription.");
            }
        } else if (role == UserRole.ADMIN || role == UserRole.OWNER) {
            // Admins/Owners: full access (no additional checks needed)
        } else {
            // Any other role (if exists) - deny by default
            throw new SecurityException("Access denied for this role.");
        }

        return prescription;
    }

    /**
     * Retrieves all active prescriptions for a given patient, if allowed for the requesting user.
     * Doctors can retrieve prescriptions for their patients; patients can retrieve their own; admins/owners can retrieve any.
     */
    public List<Prescription> getPrescriptionsByPatient(Long patientId, User currentUser) {
        // Validate patient exists (and not deleted)
        Patient patient = patientRepository.findByIdAndIsDeletedFalse(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        UserRole role = currentUser.getRole();
        if (role == UserRole.PATIENT) {
            // Patients can only fetch their own prescriptions
            if (!patient.getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied: cannot view other patients' prescriptions.");
            }
        } else if (role == UserRole.DOCTOR) {
            Doctor doctor = (Doctor) currentUser;
            // Ensure this patient is under the doctor's care
            boolean hasRelationship = appointmentRepository
                    .existsByDoctorIdAndPatientIdAndIsDeletedFalse(doctor.getId(), patientId);
            if (!hasRelationship) {
                throw new SecurityException("Access denied: patient is not under this doctor's care.");
            }
        } else if (role == UserRole.ADMIN || role == UserRole.OWNER) {
            // admins/owners can view any patient's prescriptions
        } else {
            throw new SecurityException("Access denied for this role.");
        }
        // Fetch and return all active prescriptions for the patient
        return prescriptionRepository.findByPatientIdAndIsDeletedFalse(patientId);
    }

    /**
     * Cancels (soft-deletes) an active prescription. Only the prescribing doctor or an admin/owner can cancel.
     */
    @Transactional
    public void cancelPrescription(Long prescriptionId, User currentUser) {
        Prescription prescription = prescriptionRepository.findByIdAndIsDeletedFalse(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found or already canceled."));

        UserRole role = currentUser.getRole();
        if (role == UserRole.DOCTOR) {
            // Only the doctor who issued it can cancel it (or, depending on policy, any doctor for that patient -- here we restrict to issuer)
            if (!prescription.getDoctor().getId().equals(currentUser.getId())) {
                throw new SecurityException("Access denied: only the prescriber can cancel this prescription.");
            }
        } else if (role == UserRole.ADMIN || role == UserRole.OWNER) {
            // Admin/Owner can cancel any prescription
        } else {
            // Patients (and other roles) are not allowed to cancel prescriptions
            throw new SecurityException("Access denied: patients cannot cancel prescriptions.");
        }

        // Perform soft delete
        prescription.setDeleted(true);
        prescription.setCancelledBy(currentUser);
        prescriptionRepository.save(prescription);

        // Log the cancellation action
        String prevData = String.format("Medication: %s, Dosage: %s, RefillsLeft: %d (active)",
                prescription.getMedicationName(), prescription.getDosage(), prescription.getRefillsLeft());
        auditLogService.logAction(
                "Prescription Cancelled",
                currentUser.getEmail(),
                currentUser.getRole().name(),
                "Prescription ID: " + prescriptionId,
                prevData,
                null   // after cancellation, prescription is considered deleted (no new data state)
        );
    }

    public void requestRefill(Long prescriptionId, User patient) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found."));
        if (!prescription.getPatient().getId().equals(patient.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to request this refill.");
        if (prescription.isDeleted() || prescription.getRefillsLeft() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot request refill: prescription expired or depleted.");
        if (Boolean.TRUE.equals(prescription.isRefillRequested()) && "PENDING".equals(prescription.getRefillStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refill already requested and pending.");

        prescription.setRefillRequested(true);
        prescription.setRefillStatus("PENDING");
        prescription.setRefillRequestDate(LocalDateTime.now());
        prescriptionRepository.save(prescription);
        auditLogService.logAction(
                "Refill Requested",
                patient.getEmail(),
                patient.getRole().name(),
                "Prescription ID: " + prescriptionId,
                null,
                "Refill requested"
        );
    }


    public void approveRefill(Long prescriptionId, Doctor doctor, boolean approve) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found."));
        if (!prescription.getDoctor().getId().equals(doctor.getId()))
            throw new RuntimeException("Unauthorized.");
        if (!Boolean.TRUE.equals(prescription.isRefillRequested()) ||
                !"PENDING".equals(prescription.getRefillStatus()))
            throw new RuntimeException("No pending refill request.");

        prescription.setRefillStatus(approve ? "APPROVED" : "DENIED");
        prescription.setRefillRequested(false);
        prescription.setRefillResponseDate(LocalDateTime.now());
        if (approve && prescription.getRefillsLeft() > 0) {
            prescription.setRefillsLeft(prescription.getRefillsLeft() - 1);
        }
        prescriptionRepository.save(prescription);
    }

    /**
     * Get all prescriptions issued by the given doctor (not deleted).
     */
    public List<Prescription> getPrescriptionsForDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctorIdAndIsDeletedFalse(doctorId);
    }

    /**
     * Get all prescriptions for this doctor that have a pending refill request.
     */
    public List<Prescription> getPendingRefillRequests(Long doctorId) {
        return prescriptionRepository
                .findByDoctorIdAndRefillRequestedTrueAndRefillStatus(doctorId, "PENDING");
    }
}


