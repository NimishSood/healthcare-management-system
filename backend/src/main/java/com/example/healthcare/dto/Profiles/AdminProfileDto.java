package com.example.healthcare.dto.Profiles;

import lombok.Data;

@Data
public class AdminProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String accountStatus;
}
