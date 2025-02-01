package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.exception.AppointmentNotFoundException;
import com.example.healthcare.exception.DoctorNotFoundException;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;



    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            // Validate request fields
            if (request.getDoctorId() == null || request.getPatientId() == null || request.getAppointmentTime() == null) {
                return ResponseEntity.badRequest().body("Doctor ID, Patient ID, and Appointment Time are required.");
            }

            // Fetch doctor and patient
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId()));
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + request.getPatientId()));

            // Create and book appointment
            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setAppointmentTime(request.getAppointmentTime());

            Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookedAppointment);
        } catch (DoctorNotFoundException | PatientNotFoundException e) {
            // Catch and handle the DoctorNotFoundException and PatientNotFoundException
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AppointmentConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (AppointmentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateAppointment(@PathVariable Long id, @RequestBody Appointment updated) {
        try {
            appointmentService.updateAppointment(id, updated);
            return ResponseEntity.ok("Appointment updated successfully");
        } catch (AppointmentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok("Appointment canceled");
        } catch (AppointmentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}