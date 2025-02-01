package com.example.healthcare.controller;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PostMapping
    public ResponseEntity<String> createDoctor(@RequestBody Doctor doctor) {
        doctorService.createDoctor(doctor);
        return ResponseEntity.ok("Doctor created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok("Doctor updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor deleted successfully");
    }
}