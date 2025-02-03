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
public class AuthService
{


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.OWNER) {
            throw new IllegalArgumentException("Admins and Owners cannot self-register.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ Correctly instantiate subclass
        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = new Patient();  // ✅ Create a Patient object
            patient.setFirstName(user.getFirstName());
            patient.setLastName(user.getLastName());
            patient.setEmail(user.getEmail());
            patient.setPassword(user.getPassword());
            patient.setPhoneNumber(user.getPhoneNumber());
            patient.setRole(UserRole.PATIENT);

            userRepository.save(patient);  // ✅ Saves with dtype = 'Patient'
        } else {
            userRepository.save(user);
        }
    }


    // ✅ Authenticate user (Login)
    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<User> userOptional = userRepository.findByEmail(authRequest.getEmail());

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(authRequest.getPassword(), userOptional.get().getPassword())) {
            throw new UserNotFoundException("Invalid email or password.");
        }

        User user = userOptional.get();
        return new AuthResponse("Login successful", user.getRole().name());
    }

    // ✅ Forgot Password (Send reset link)
    public void sendPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // Simulate sending a password reset link (Implement proper email handling later)
        System.out.println("Password reset link sent to: " + email);
    }
}
