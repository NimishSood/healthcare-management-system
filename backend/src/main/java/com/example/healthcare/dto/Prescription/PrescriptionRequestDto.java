package com.example.healthcare.dto.Prescription;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequestDto {
    private Long patientId;
    private Long appointmentId; // nullable
    private String medicationName;
    private String dosage;
    private String instructions;
    private int refillsLeft;
}
