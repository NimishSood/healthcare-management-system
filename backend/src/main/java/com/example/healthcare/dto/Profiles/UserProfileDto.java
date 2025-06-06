package com.example.healthcare.dto.Profiles;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    // ...plus avatar if you have it
}
