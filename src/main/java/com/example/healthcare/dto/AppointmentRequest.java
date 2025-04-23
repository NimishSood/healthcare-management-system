package com.example.healthcare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Represents a request to book an appointment.
 */
@Getter
@Setter
@ToString
public class AppointmentRequest {

    /**
     * The ID of the doctor for the appointment.
     */
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    /**
     * The ID of the patient for the appointment.
     */
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    /**
     * The date and time of the appointment.
     */
    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;
}