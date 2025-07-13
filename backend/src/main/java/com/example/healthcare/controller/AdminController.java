package com.example.healthcare.controller;

import com.example.healthcare.dto.Profiles.AdminProfileDto;
import com.example.healthcare.dto.Profiles.ProfileMapper;
import com.example.healthcare.entity.*;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.dto.Appointments.AppointmentDto;
import com.example.healthcare.service.AdminService;
import com.example.healthcare.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    // ✅ View Admin Profile
    @GetMapping("/profile")
    public AdminProfileDto viewAdminProfile() {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        return ProfileMapper.toAdminDto(admin);
    }

    // ✅ Update Admin Profile
    @PutMapping("/profile")
    public String updateAdminProfile(@RequestBody Admin updatedAdmin) {
        Admin authenticatedAdmin = securityUtils.getAuthenticatedAdmin();
        adminService.updateAdminProfile(authenticatedAdmin.getId(), updatedAdmin);
        return "Admin profile updated successfully.";
    }

    // ✅ Admin can add a Doctor
    @PostMapping("/add-doctor")
    public String addDoctor(@RequestBody Doctor doctor) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        adminService.addDoctor(admin, doctor);
        return "Doctor added successfully.";
    }

    // ✅ Admin can add a Patient
    @PostMapping("/add-patient")
    public String addPatient(@RequestBody Patient patient) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        adminService.addPatient(admin, patient);
        return "Patient added successfully.";
    }

    // ✅ Admin can remove a Doctor (Soft Delete)
    @DeleteMapping("/remove-doctor/{doctorId}")
    public String removeDoctor(@PathVariable Long doctorId) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        adminService.softDeleteDoctor(admin, doctorId);
        return "Doctor removed successfully (Soft Delete).";
    }

    // ✅ Admin can remove a Patient (Soft Delete)
    @DeleteMapping("/remove-patient/{patientId}")
    public String removePatient(@PathVariable Long patientId) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        adminService.softDeletePatient(admin, patientId);
        return "Patient removed successfully (Soft Delete).";
    }

    // ✅ View All Assigned Doctors
    @GetMapping("/view-doctors")
    public List<Doctor> viewDoctors() {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        return adminService.getAllDoctors(admin);
    }

    // ✅ View All Assigned Patients
    @GetMapping("/view-patients")
    public List<Patient> viewPatients() {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        return adminService.getAllPatients(admin);
    }

    // ✅ Admin can view all Appointments
    @GetMapping("/view-appointments")
    public List<AppointmentDto> viewAppointments() {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        return adminService.getAllAppointments(admin);
    }

    // ✅ Reactivate a Doctor
    @PutMapping("/reactivate-doctor/{doctorId}")
    public ResponseEntity<String> reactivateDoctor(@PathVariable Long doctorId) {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        adminService.reactivateDoctor(admin, doctorId);
        return ResponseEntity.ok("Doctor reactivated successfully.");
    }

    // ✅ Get Audit Logs (Admin only)
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        Admin admin = securityUtils.getAuthenticatedAdmin();
        return ResponseEntity.ok(adminService.getAuditLogs(admin));
    }
}
