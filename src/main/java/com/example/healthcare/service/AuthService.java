package com.example.healthcare.service;

import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        user.setPassword(user.getPassword());  // Store plain text (TEMPORARY)
        if (user.getRole() == null) {
            user.setRole(UserRole.PATIENT);
        }
        userRepository.save(user);
    }

    public Optional<User> login(String email, String rawPassword) {
        // Find the user by email and ensure it's not marked as deleted
        Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(email);

        if (userOpt.isPresent()) {
            User found = userOpt.get();
            // Use BCrypt to match rawPassword with the hashed password stored in the database
            if (passwordEncoder.matches(rawPassword, found.getPassword())) {
                return Optional.of(found); // Return the user if password matches
            }
        }
        return Optional.empty(); // Return empty if no match or user not found
    }




}