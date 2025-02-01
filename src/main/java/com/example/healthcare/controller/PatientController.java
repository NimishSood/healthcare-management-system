package com.example.healthcare.controller;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    public ResponseEntity<String> createPatient(@RequestBody Patient patient) {
        patientService.createPatient(patient);
        return ResponseEntity.ok("Patient created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        patientService.updatePatient(id, patient);
        return ResponseEntity.ok("Patient updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }
}