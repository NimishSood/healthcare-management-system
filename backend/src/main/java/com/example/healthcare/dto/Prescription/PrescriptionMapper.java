package com.example.healthcare.dto.Prescription;

import com.example.healthcare.entity.Prescription;

public class PrescriptionMapper {
    public static PrescriptionDto toDto(Prescription p) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(p.getId());
        if (p.getPatient() != null) dto.setPatientId(p.getPatient().getId());
        if (p.getDoctor() != null) dto.setDoctorId(p.getDoctor().getId());
        dto.setMedicationName(p.getMedicationName());
        dto.setDosage(p.getDosage());
        dto.setInstructions(p.getInstructions());
        dto.setRefillsLeft(p.getRefillsLeft());
        dto.setDateIssued(p.getDateIssued());
        dto.setDeleted(p.isDeleted());
        dto.setRefillRequested(p.isRefillRequested());
        dto.setRefillStatus(p.getRefillStatus());
        return dto;
    }
}