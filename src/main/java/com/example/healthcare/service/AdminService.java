package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.exception.AdminNotFoundException;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdminRepository adminRepository;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service
    private final PasswordEncoder passwordEncoder;

    private void verifyActiveAdmin(User admin) {
        if (!admin.getRole().isAdmin()) {
            throw new UnauthorizedAccessException("Only Admins can perform this action.");
        }
        if (admin.isDeleted()) {
            throw new UnauthorizedAccessException("Inactive Admins cannot perform this action.");
        }
    }

    @Transactional
    public void addDoctor(User admin, Doctor doctor) {
        verifyActiveAdmin(admin);
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctorRepository.save(doctor);

        // ✅ Log the action
        auditLogService.logAction(
                "Doctor Added", admin.getEmail(), "ADMIN",
                "Doctor ID: " + doctor.getId(), null, doctor.toString()
        );
    }

    @Transactional
    public void addPatient(User admin, Patient patient) {
        verifyActiveAdmin(admin);
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientRepository.save(patient);

        auditLogService.logAction(
                "Patient Added", admin.getEmail(), "ADMIN",
                "Patient ID: " + patient.getId(), null, patient.toString()
        );
    }

    @Transactional
    public void removeDoctor(User admin, Long doctorId) {
        verifyActiveAdmin(admin);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        doctor.setDeleted(true);
        doctorRepository.save(doctor);

        auditLogService.logAction(
                "Doctor Removed", admin.getEmail(), "ADMIN",
                "Doctor ID: " + doctorId, doctor.toString(), null
        );
    }

    @Transactional
    public void removePatient(User admin, Long patientId) {
        verifyActiveAdmin(admin);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        patient.setDeleted(true);
        patientRepository.save(patient);

        auditLogService.logAction(
                "Patient Removed", admin.getEmail(), "ADMIN",
                "Patient ID: " + patientId, patient.toString(), null
        );
    }

    public List<Doctor> getAllDoctors(User admin) {
        verifyActiveAdmin(admin);
        return doctorRepository.findAllByIsDeletedFalse();
    }

    public List<Patient> getAllPatients(User admin) {
        verifyActiveAdmin(admin);
        return patientRepository.findAllByIsDeletedFalse();
    }

    public List<Appointment> getAllAppointments(User admin) {
        verifyActiveAdmin(admin);
        return appointmentRepository.findAll();
    }

    @Transactional
    public void reactivateDoctor(User admin, Long doctorId) {
        verifyActiveAdmin(admin);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        if (!doctor.isDeleted()) {
            throw new IllegalStateException("Doctor is already active.");
        }

        doctor.setDeleted(false);
        doctorRepository.save(doctor);

        auditLogService.logAction(
                "Doctor Reactivated", admin.getEmail(), "ADMIN",
                "Doctor ID: " + doctorId, null, doctor.toString()
        );
    }

    @Transactional
    public void updateAdminProfile(Long adminId, Admin updatedAdmin) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));

        String previousData = admin.toString();

        if (updatedAdmin.getFirstName() != null) {
            admin.setFirstName(updatedAdmin.getFirstName());
        }
        if (updatedAdmin.getLastName() != null) {
            admin.setLastName(updatedAdmin.getLastName());
        }
        if (updatedAdmin.getPhoneNumber() != null) {
            admin.setPhoneNumber(updatedAdmin.getPhoneNumber());
        }

        adminRepository.save(admin);

        auditLogService.logAction(
                "Admin Profile Updated", admin.getEmail(), "ADMIN",
                "Admin ID: " + adminId, previousData, admin.toString()
        );
    }

    @Transactional
    public void softDeleteDoctor(Admin admin, Long doctorId) {
        verifyActiveAdmin(admin);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        if (doctor.isDeleted())
        {
            throw new IllegalStateException("Doctor is already deleted.");
        }

        doctor.setDeleted(true);
        doctorRepository.save(doctor);

        // ✅ Audit Log Entry
        auditLogService.logAction(
                "Doctor Soft Deleted",
                admin.getEmail(),
                admin.getRole().name(),
                "Doctor ID: " + doctorId,
                "Active",
                "Deleted"
        );
    }

    @Transactional
    public void softDeletePatient(Admin admin, Long patientId)
    {
        verifyActiveAdmin(admin);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        if (patient.isDeleted()) {
            throw new IllegalStateException("Patient is already deleted.");
        }

        patient.setDeleted(true);
        patientRepository.save(patient);

        // ✅ Audit Log Entry
        auditLogService.logAction(
                "Patient Soft Deleted",
                admin.getEmail(),
                admin.getRole().name(),
                "Patient ID: " + patientId,
                "Active",
                "Deleted"
        );
    }

    public List<AuditLog> getAuditLogs(Admin admin)
    {
        verifyActiveAdmin(admin); // ✅ Ensure Admin is Active & Not Deleted

        // ✅ Fetch all audit logs (Admins can view full logs)
        return auditLogService.getAllLogs();
    }

}
