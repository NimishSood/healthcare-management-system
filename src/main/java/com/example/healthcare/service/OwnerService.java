package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.entity.enums.UserRole;
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
public class OwnerService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service

    @Transactional
    public void addAdmin(Owner owner, Admin admin) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can add Admins.");
        }

        admin.setRole(UserRole.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

        auditLogService.logAction(
                "Admin Added", owner.getEmail(), "OWNER",
                "Admin ID: " + admin.getId(), null, admin.toString()
        );
    }

    @Transactional
    public void softDeleteAdmin(Owner owner, Long adminId) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can remove Admins.");
        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + adminId));

        if (admin.isDeleted()) {
            throw new IllegalStateException("Admin is already deleted.");
        }

        admin.setDeleted(true);
        adminRepository.save(admin);

        auditLogService.logAction(
                "Admin Removed", owner.getEmail(), "OWNER",
                "Admin ID: " + adminId, admin.toString(), null
        );
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAllByIsDeletedFalse();
    }

    @Transactional
    public void updateOwnerProfile(Owner owner, Owner updatedOwner) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can update their profile.");
        }

        String previousData = owner.toString();

        if (updatedOwner.getFirstName() != null) {
            owner.setFirstName(updatedOwner.getFirstName());
        }
        if (updatedOwner.getLastName() != null) {
            owner.setLastName(updatedOwner.getLastName());
        }
        if (updatedOwner.getEmail() != null) {
            owner.setEmail(updatedOwner.getEmail());
        }
        if (updatedOwner.getPhoneNumber() != null) {
            owner.setPhoneNumber(updatedOwner.getPhoneNumber());
        }

        userRepository.save(owner);

        auditLogService.logAction(
                "Owner Profile Updated", owner.getEmail(), "OWNER",
                "Owner ID: " + owner.getId(), previousData, owner.toString()
        );
    }

    @Transactional
    public void reactivateAdmin(Owner owner, Long adminId) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can reactivate Admins.");
        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

        if (!admin.isDeleted()) {
            throw new IllegalStateException("Admin is already active.");
        }

        admin.setDeleted(false);
        adminRepository.save(admin);

        auditLogService.logAction(
                "Admin Reactivated", owner.getEmail(), "OWNER",
                "Admin ID: " + adminId, null, admin.toString()
        );
    }

    // ✅ Update Admin Details
    @Transactional
    public void updateAdmin(Long adminId, Admin updatedAdmin) {
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
                "Admin Updated", "OWNER", "OWNER",
                "Admin ID: " + adminId, previousData, admin.toString()
        );
    }

    // ✅ Retrieve a Specific Admin by ID
    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId)
                .filter(admin -> !admin.isDeleted())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found or has been deleted."));
    }

    // ✅ Retrieve Audit Logs for the Owner
    public List<AuditLog> getAuditLogs(Owner owner) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can view audit logs.");
        }
        return auditLogService.getAllLogs(); // Owners have full access to logs
    }

    // ✅ Reactivate a Soft-Deleted Doctor
    @Transactional
    public void reactivateDoctor(Owner owner, Long doctorId) {
        if (!owner.getRole().isOwner()) {
            throw new UnauthorizedAccessException("Only Owners can reactivate Doctors.");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        if (!doctor.isDeleted()) {
            throw new IllegalStateException("Doctor is already active.");
        }

        doctor.setDeleted(false);
        doctorRepository.save(doctor);

        auditLogService.logAction(
                "Doctor Reactivated", owner.getEmail(), "OWNER",
                "Doctor ID: " + doctorId, null, doctor.toString()
        );
    }
}
