package com.example.healthcare.controller;

import com.example.healthcare.dto.OwnerProfileDto;
import com.example.healthcare.dto.ProfileMapper;
import com.example.healthcare.entity.Admin;
import com.example.healthcare.entity.AuditLog;
import com.example.healthcare.entity.Owner;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AuditLogService;
import com.example.healthcare.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    // ✅ Owner can add an Admin
    @PostMapping("/add-admin")
    public String addAdmin(@RequestBody Admin admin) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.addAdmin(owner, admin);
        return "Admin added successfully.";
    }

    // ✅ Owner can remove an Admin (Soft Delete)
    @DeleteMapping("/remove-admin/{adminId}")
    public String removeAdmin(@PathVariable Long adminId) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.softDeleteAdmin(owner, adminId);
        return "Admin removed successfully (Soft Delete).";
    }

    // ✅ Owner can view all Admins
    @GetMapping("/view-admins")
    public List<Admin> viewAdmins() {
        Owner owner = securityUtils.getAuthenticatedOwner();
        return ownerService.getAllAdmins();
    }

    // ✅ View a Single Admin by ID
    @GetMapping("/admin/{adminId}")
    public Admin getAdminById(@PathVariable Long adminId) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        return ownerService.getAdminById(adminId);
    }

    // ✅ Edit an Admin's Details
    @PutMapping("/edit-admin/{adminId}")
    public String editAdmin(@PathVariable Long adminId, @RequestBody Admin updatedAdmin) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.updateAdmin(adminId, updatedAdmin);
        return "Admin details updated successfully.";
    }

    // ✅ View Owner Profile
    @GetMapping("/profile")
    public OwnerProfileDto viewOwnerProfile() {
        Owner owner = securityUtils.getAuthenticatedOwner();
        return ProfileMapper.toOwnerDto(owner);
    }

    // ✅ Edit Owner Profile
    @PutMapping("/profile")
    public String editOwnerProfile(@RequestBody Owner updatedOwner) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.updateOwnerProfile(owner, updatedOwner);
        return "Owner profile updated successfully.";
    }

    // ✅ Reactivate a Soft-Deleted Admin
    @PutMapping("/reactivate-admin/{adminId}")
    public ResponseEntity<String> reactivateAdmin(@PathVariable Long adminId) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.reactivateAdmin(owner, adminId);
        return ResponseEntity.ok("Admin reactivated successfully.");
    }

    // ✅ Reactivate a Soft-Deleted Doctor
    @PutMapping("/reactivate-doctor/{doctorId}")
    public ResponseEntity<String> reactivateDoctor(@PathVariable Long doctorId) {
        Owner owner = securityUtils.getAuthenticatedOwner();
        ownerService.reactivateDoctor(owner, doctorId);
        return ResponseEntity.ok("Doctor reactivated successfully.");
    }

    // ✅ Get Audit Logs
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        Owner owner = securityUtils.getAuthenticatedOwner();
        return ResponseEntity.ok(ownerService.getAuditLogs(owner));
    }
}
