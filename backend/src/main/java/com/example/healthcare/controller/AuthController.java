package com.example.healthcare.controller;

import com.example.healthcare.dto.Authorization.AuthRequest;
import com.example.healthcare.dto.Authorization.AuthResponse;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{


    private final AuthService authService;

    // ✅ Register a new user (Only Patients can register)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.OWNER || user.getRole() == UserRole.DOCTOR)
        {
            return ResponseEntity.badRequest().body("Admins, Owner and Doctrors cannot self-register.");
        }
        authService.registerUser(user);
        return ResponseEntity.status(201).body("Patient registered successfully");
    }

    // ✅ Login (Returns success or failure)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest)
    {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }

    // ✅ Forgot Password (Send reset link)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendPasswordReset(email);
        return ResponseEntity.ok("Password reset link sent.");
    }

    //NEW
    // Add to AuthController.java
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(authService.verifyToken(authHeader));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }


}
