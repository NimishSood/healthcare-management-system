// src/main/java/com/example/healthcare/dto/Doctors/DoctorDto.java
package com.example.healthcare.dto.Doctors;

import lombok.Data;

@Data
public class DoctorDto {
    private Long   id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String phoneNumber;
    private String email;
}
