package com.example.healthcare.dto.Authorization;

import com.example.healthcare.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String role;
    private String token;
    private Long userId; // New field
    private String email; // New field
    private String firstName; // New field
    private String lastName; // New field

    // Update constructor
    public AuthResponse(String message, String role, String token, User user) {
        this.message = message;
        this.role = role;
        this.token = token;
        this.userId = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}