package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.AppointmentConflictException;
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
        // Fetch doctor and patient from the database
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + request.getPatientId()));

        // Create the appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(request.getAppointmentTime());

        // Book the appointment
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookedAppointment);
        } catch (AppointmentConflictException e) {
            throw e; // Propagate the exception to the GlobalExceptionHandler
        }
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAppointment(@PathVariable Long id, @RequestBody Appointment updated) {
        appointmentService.updateAppointment(id, updated);
        return ResponseEntity.ok("Appointment updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok("Appointment canceled");
    }
}