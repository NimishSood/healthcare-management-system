package com.example.healthcare.service;

import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);

        if (user.getRole() == null) {
            user.setRole(UserRole.PATIENT);
        }

        userRepository.save(user);
    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(email);

        if (userOpt.isPresent()) {
            User found = userOpt.get();
            if (passwordEncoder.matches(rawPassword, found.getPassword())) {
                return Optional.of(found);
            }
        }

        return Optional.empty();
    }
}