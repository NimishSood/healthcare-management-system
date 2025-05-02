package com.example.healthcare.controller;

import com.example.healthcare.dto.Profiles.PatientProfileDto;
import com.example.healthcare.dto.Profiles.ProfileMapper;
import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final SecurityUtils securityUtils;

    // ✅ View Patient Profile
    @GetMapping("/profile")
    public PatientProfileDto getPatientProfile() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return ProfileMapper.toPatientDto(patient);
    }

    // ✅ Update Profile
    @PutMapping("/profile")
    public String updatePatientProfile(@RequestBody Patient patient) {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        patientService.updatePatient(authenticatedPatient.getId(), patient);
        return "Patient profile updated successfully.";
    }

    // ✅ Soft Delete Account
    @DeleteMapping("/delete-account")
    public String deletePatientAccount() {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        patientService.softDeletePatient(authenticatedPatient.getId());
        return "Patient account deleted successfully.";
    }

    // ✅ View Upcoming and Past Appointments
    @GetMapping("/appointments/upcoming")
    public List<Appointment> getUpcomingAppointments() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return appointmentService.getUpcomingAppointmentsByPatient(patient.getId());
    }

    @GetMapping("/appointments/history")
    public List<Appointment> getPastAppointments() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return appointmentService.getPastAppointmentsByPatient(patient.getId());
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam LocalDateTime appointmentTime) {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        appointmentService.bookAppointment(authenticatedPatient.getId(), doctorId, appointmentTime);
        return "Appointment booked successfully.";
    }

    // ✅ Cancel an Appointment
    @DeleteMapping("/appointments/cancel")
    public String cancelAppointment(@RequestParam Long appointmentId) {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        appointmentService.cancelAppointment(authenticatedPatient.getId(), appointmentId);
        return "Appointment cancelled successfully.";
    }

    // ✅ View Prescriptions
    @GetMapping("/prescriptions")
    public List<?> getPatientPrescriptions() {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        return patientService.getPrescriptions(authenticatedPatient.getId());
    }

    // ✅ Request a Prescription Refill
    @PostMapping("/prescriptions/request-refill")
    public String requestPrescriptionRefill(@RequestParam Long prescriptionId) {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        patientService.requestRefill(authenticatedPatient.getId(), prescriptionId);
        return "Prescription refill request submitted.";
    }

    // ✅ View Messages
    @GetMapping("/messages")
    public List<?> getPatientMessages() {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        return patientService.getMessages(authenticatedPatient.getId());
    }

    // ✅ Send Message to Doctor
    @PostMapping("/messages/send")
    public String sendMessageToDoctor(@RequestParam Long doctorId,
                                      @RequestBody String message) {
        Patient authenticatedPatient = securityUtils.getAuthenticatedPatient();
        patientService.sendMessage(authenticatedPatient.getId(), doctorId, message);
        return "Message sent successfully.";
    }
}
