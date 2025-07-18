package com.example.healthcare.controller;

import com.example.healthcare.entity.MedicalRecord;
import com.example.healthcare.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public MedicalRecord create(@RequestBody MedicalRecord request) {
        return medicalRecordService.createMedicalRecord(
                request.getPatient().getId(),
                request.getDoctor() != null ? request.getDoctor().getId() : null,
                request.getAppointment() != null ? request.getAppointment().getId() : null,
                request.getDescription()
        );
    }

    @GetMapping("/{id}")
    public MedicalRecord get(@PathVariable Long id) {
        return medicalRecordService.getMedicalRecord(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecord> forPatient(@PathVariable Long patientId) {
        return medicalRecordService.getRecordsForPatient(patientId);
    }

    @PutMapping("/{id}")
    public MedicalRecord update(@PathVariable Long id, @RequestBody MedicalRecord request) {
        return medicalRecordService.updateMedicalRecord(id, request.getDescription());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
    }
}