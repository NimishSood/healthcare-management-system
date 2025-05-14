package com.example.healthcare.service;

import com.example.healthcare.dto.Authorization.AuthRequest;
import com.example.healthcare.dto.Authorization.AuthResponse;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.AccountStatus;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ComponentScan("com.example.healthcare.config")
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final JwtService jwtService;

    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            auditLogService.logAction(
                    "Registration Failed", user.getEmail(), user.getRole().name(),
                    "User attempted to register", "Reason: Email already in use", null
            );
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (user.getRole() == UserRole.ADMIN
                || user.getRole() == UserRole.OWNER
                || user.getRole() == UserRole.DOCTOR) {
            auditLogService.logAction(
                    "Registration Denied", user.getEmail(), user.getRole().name(),
                    "User attempted to register", "Reason: Cannot self-register as Admin/Owner/Doctor", null
            );
            throw new IllegalArgumentException("Admins, Owners and Doctors cannot self-register.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setFirstName(user.getFirstName());
            patient.setLastName(user.getLastName());
            patient.setEmail(user.getEmail());
            patient.setPassword(user.getPassword());
            patient.setPhoneNumber(user.getPhoneNumber());
            patient.setRole(UserRole.PATIENT);
            userRepository.save(patient);
        }

        auditLogService.logAction(
                "Patient Registered", user.getEmail(), user.getRole().name(),
                "New patient account created", null, user.toString()
        );
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        // only non-deleted users
        User user = userRepository
                .findByEmailAndIsDeletedFalse(authRequest.getEmail())
                .orElseThrow(() -> {
                    auditLogService.logAction(
                            "Failed Login Attempt", authRequest.getEmail(), "UNKNOWN",
                            "Invalid login attempt", "Reason: No such user or deleted", null
                    );
                    return new UserNotFoundException("Invalid email or password.");
                });

        // block deactivated users
        if (user.getAccountStatus() == AccountStatus.DEACTIVATED) {
            auditLogService.logAction(
                    "Blocked Login Attempt", user.getEmail(), user.getRole().name(),
                    "Invalid login attempt", "Reason: Account deactivated", null
            );
            throw new IllegalStateException("Account has been deactivated.");
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            auditLogService.logAction(
                    "Failed Login Attempt", user.getEmail(), user.getRole().name(),
                    "Invalid login attempt", "Reason: Bad credentials", null
            );
            throw new UserNotFoundException("Invalid email or password.");
        }

        String token = jwtService.generateToken(user);
        auditLogService.logAction(
                "User Logged In", user.getEmail(), user.getRole().name(),
                "User successfully authenticated", null, null
        );

        return new AuthResponse(
                "Login successful",
                user.getRole().name(),
                token,
                user
        );
    }

    public void sendPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            auditLogService.logAction(
                    "Password Reset Request Failed", email, "UNKNOWN",
                    "User attempted to reset password", "Reason: Email not found", null
            );
            throw new UserNotFoundException("User not found with email: " + email);
        }
        System.out.println("Password reset link sent to: " + email);
        auditLogService.logAction(
                "Password Reset Requested", email, userOpt.get().getRole().name(),
                "User requested password reset", null, null
        );
    }

    public AuthResponse verifyToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        User user = userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        auditLogService.logAction(
                "Token Verified", user.getEmail(), user.getRole().name(),
                "Session validation", null, null
        );

        return new AuthResponse(
                "Token valid",
                user.getRole().name(),
                token,
                user
        );
    }
}
