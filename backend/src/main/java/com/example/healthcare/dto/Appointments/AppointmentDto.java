package com.example.healthcare.dto.Appointments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;
    private String doctorName;
    private String specialty;
    private String patientName;
    private LocalDateTime appointmentTime;
    private String status;
    private LocalDateTime createdAt;      // Booked On
    private LocalDateTime updatedAt;      // Last Updated
    private String cancelledByName;       // who cancelled
    private String location;              // e.g. “Room 101” or URL
    private String notes;                 // reason for visit
    private String doctorContact;         // e.g. email or phone
}
