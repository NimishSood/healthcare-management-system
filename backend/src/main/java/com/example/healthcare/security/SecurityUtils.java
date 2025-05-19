package com.example.healthcare.security;

import com.example.healthcare.entity.*;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for safely retrieving the authenticated user
 * and casting to appropriate roles: Patient, Admin, Doctor, Owner.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserService userService;

    /**
     * Get the currently authenticated user.
     */
    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userService.getUserByEmail(userDetails.getUsername());
        }
        throw new UnauthorizedAccessException("Unauthorized: No valid user found.");
    }

    /**
     * Get authenticated Patient.
     */
    public Patient getAuthenticatedPatient() {
        User user = getAuthenticatedUser();
        if (user instanceof Patient patient) {
            return patient;
        }
        throw new UnauthorizedAccessException("Only Patients can access this resource.");
    }

    /**
     * Get authenticated Admin.
     */
    public Admin getAuthenticatedAdmin() {
        User user = getAuthenticatedUser();
        if (user instanceof Admin admin) {
            return admin;
        }
        throw new UnauthorizedAccessException("Only Admins can access this resource.");
    }

    /**
     * Get authenticated Doctor.
     */
    public Doctor getAuthenticatedDoctor() {
        User user = getAuthenticatedUser();
        if (user instanceof Doctor doctor) {
            return doctor;
        }
        throw new UnauthorizedAccessException("Only Doctors can access this resource.");
    }

    /**
     * Get authenticated Owner.
     */
    public Owner getAuthenticatedOwner() {
        User user = getAuthenticatedUser();
        if (user instanceof Owner owner) {
            return owner;
        }
        throw new UnauthorizedAccessException("Only Owners can access this resource.");
    }

    public Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No authenticated user");
        }
        User user = (User) auth.getPrincipal();
        return user.getId();
    }
}
