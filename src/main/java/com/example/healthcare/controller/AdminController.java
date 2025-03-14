package com.example.healthcare.controller;

import com.example.healthcare.entity.*;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.service.AdminService;
import com.example.healthcare.service.AuditLogService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController
{

    private final AdminService adminService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    // ✅ Helper method to get authenticated Admin
    private Admin getAuthenticatedAdmin(UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user instanceof Admin admin) {
            return admin;
        }
        throw new UnauthorizedAccessException("Only an Admin can access this resource.");
    }

    // ✅ View Admin Profile
    @GetMapping("/profile")
    public Admin viewAdminProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return getAuthenticatedAdmin(userDetails);
    }

    // ✅ Update Admin Profile (Supports Partial Updates)
    @PutMapping("/profile")
    public String updateAdminProfile(@RequestBody Admin updatedAdmin,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Admin authenticatedAdmin = getAuthenticatedAdmin(userDetails);
        adminService.updateAdminProfile(authenticatedAdmin.getId(), updatedAdmin);
        return "Admin profile updated successfully.";
    }

    // ✅ Admin can add a Doctor
    @PostMapping("/add-doctor")
    public String addDoctor(@RequestBody Doctor doctor, @AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        adminService.addDoctor(admin, doctor);
        return "Doctor added successfully.";
    }

    // ✅ Admin can add a Patient
    @PostMapping("/add-patient")
    public String addPatient(@RequestBody Patient patient, @AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        adminService.addPatient(admin, patient);
        return "Patient added successfully.";
    }

    // ✅ Admin can remove a Doctor (Soft Delete)
    @DeleteMapping("/remove-doctor/{doctorId}")
    public String removeDoctor(@PathVariable Long doctorId, @AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        adminService.softDeleteDoctor(admin, doctorId);
        return "Doctor removed successfully (Soft Delete).";
    }

    // ✅ Admin can remove a Patient (Soft Delete)
    @DeleteMapping("/remove-patient/{patientId}")
    public String removePatient(@PathVariable Long patientId, @AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        adminService.softDeletePatient(admin, patientId);
        return "Patient removed successfully (Soft Delete).";
    }

    // ✅ View All Assigned Doctors
    @GetMapping("/view-doctors")
    public List<Doctor> viewDoctors(@AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        return adminService.getAllDoctors(admin);
    }

    // ✅ View All Assigned Patients
    @GetMapping("/view-patients")
    public List<Patient> viewPatients(@AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        return adminService.getAllPatients(admin);
    }

    // ✅ Admin can view all Appointments
    @GetMapping("/view-appointments")
    public List<Appointment> viewAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        return adminService.getAllAppointments(admin);
    }

    // ✅ Reactivate a Doctor (Only Admins)
    @PutMapping("/reactivate-doctor/{doctorId}")
    public ResponseEntity<String> reactivateDoctor(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long doctorId) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        adminService.reactivateDoctor(admin, doctorId);
        return ResponseEntity.ok("Doctor reactivated successfully.");
    }

    // ✅ Get Audit Logs (Admin only)
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs(@AuthenticationPrincipal UserDetails userDetails) {
        Admin admin = getAuthenticatedAdmin(userDetails);
        return ResponseEntity.ok(adminService.getAuditLogs(admin));
    }
}
