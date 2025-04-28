package com.example.healthcare.service;

import com.example.healthcare.dto.AuthRequest;
import com.example.healthcare.dto.AuthResponse;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ComponentScan("com.example.healthcare.config")
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service
    private final JwtService jwtService;


    // ✅ User Registration
    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            auditLogService.logAction(
                    "Registration Failed", user.getEmail(), user.getRole().name(),
                    "User attempted to register", "Reason: Email already in use", null
            );
            throw new IllegalArgumentException("Email is already in use.");
        }

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.OWNER || user.getRole() == UserRole.DOCTOR) {
            auditLogService.logAction(
                    "Registration Denied", user.getEmail(), user.getRole().name(),
                    "User attempted to register", "Reason: Cannot self-register as Admin/Owner/Doctor", null
            );
            throw new IllegalArgumentException("Admins,Owners and Doctors cannot self-register.");
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

    // ✅ User Authentication (Login)
    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<User> userOptional = userRepository.findByEmail(authRequest.getEmail());

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(authRequest.getPassword(), userOptional.get().getPassword())) {

            auditLogService.logAction(
                    "Failed Login Attempt", authRequest.getEmail(), "UNKNOWN",
                    "Invalid login attempt", "Reason: Invalid credentials", null
            );

            throw new UserNotFoundException("Invalid email or password.");
        }

        User user = userOptional.get();

        auditLogService.logAction(
                "User Logged In", user.getEmail(), user.getRole().name(),
                "User successfully authenticated", null, null
        );

        // 🔥 Generate the JWT token here
        String token = jwtService.generateToken(user);

        // 🔥 Now return the AuthResponse with the token
        return new AuthResponse("Login successful", user.getRole().name(), token);
    }


    // ✅ Forgot Password
    public void sendPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            auditLogService.logAction(
                    "Password Reset Request Failed", email, "UNKNOWN",
                    "User attempted to reset password", "Reason: Email not found", null
            );
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // Simulated password reset (implement actual email logic)
        System.out.println("Password reset link sent to: " + email);

        auditLogService.logAction(
                "Password Reset Requested", email, userOptional.get().getRole().name(),
                "User requested password reset", null, null
        );
    }
}

