package com.example.healthcare.controller;

import com.example.healthcare.entity.*;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.service.AuditLogService;
import com.example.healthcare.service.OwnerService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    // ✅ Helper method to get authenticated Owner
    private Owner getAuthenticatedOwner(UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user instanceof Owner owner) {
            return owner;
        }
        throw new UnauthorizedAccessException("Only the Owner can access this resource.");
    }

    // ✅ Owner can add an Admin
    @PostMapping("/add-admin")
    public String addAdmin(@RequestBody Admin admin, @AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.addAdmin(owner, admin);
        return "Admin added successfully.";
    }

    // ✅ Owner can remove an Admin (Soft Delete)
    @DeleteMapping("/remove-admin/{adminId}")
    public String removeAdmin(@PathVariable Long adminId, @AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.softDeleteAdmin(owner, adminId);
        return "Admin removed successfully (Soft Delete).";
    }

    // ✅ Owner can view all Admins
    @GetMapping("/view-admins")
    public List<Admin> viewAdmins(@AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        return ownerService.getAllAdmins();
    }

    // ✅ View a Single Admin by ID
    @GetMapping("/admin/{adminId}")
    public Admin getAdminById(@PathVariable Long adminId, @AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        return ownerService.getAdminById(adminId);
    }

    // ✅ Edit an Admin's Details
    @PutMapping("/edit-admin/{adminId}")
    public String editAdmin(@PathVariable Long adminId, @RequestBody Admin updatedAdmin,
                            @AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.updateAdmin(adminId, updatedAdmin);
        return "Admin details updated successfully.";
    }

    // ✅ View Owner Profile
    @GetMapping("/profile")
    public Owner viewOwnerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return getAuthenticatedOwner(userDetails);
    }

    // ✅ Edit Owner Profile
    @PutMapping("/profile")
    public String editOwnerProfile(@RequestBody Owner updatedOwner, @AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.updateOwnerProfile(owner, updatedOwner);
        return "Owner profile updated successfully.";
    }

    // ✅ Reactivate a Soft-Deleted Admin (Only Owner)
    @PutMapping("/reactivate-admin/{adminId}")
    public ResponseEntity<String> reactivateAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long adminId) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.reactivateAdmin(owner, adminId);
        return ResponseEntity.ok("Admin reactivated successfully.");
    }

    // ✅ Reactivate a Soft-Deleted Doctor (Only Owner)
    @PutMapping("/reactivate-doctor/{doctorId}")
    public ResponseEntity<String> reactivateDoctor(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long doctorId) {
        Owner owner = getAuthenticatedOwner(userDetails);
        ownerService.reactivateDoctor(owner, doctorId);
        return ResponseEntity.ok("Doctor reactivated successfully.");
    }

    // ✅ Get Audit Logs (Only Owner)
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs(@AuthenticationPrincipal UserDetails userDetails) {
        Owner owner = getAuthenticatedOwner(userDetails);
        return ResponseEntity.ok(ownerService.getAuditLogs(owner));
    }
}
