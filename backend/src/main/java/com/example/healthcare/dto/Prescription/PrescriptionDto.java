package com.example.healthcare.dto.Prescription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String medicationName;
    private String dosage;
    private String instructions;
    private int refillsLeft;
    private LocalDate dateIssued;
    private boolean deleted;
    private boolean refillRequested;
    private String refillStatus;
}