package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.PatientService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    // ✅ Ensure only Patients can access their own data
    private Patient getAuthenticatedPatient(UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());

        if (!(user instanceof Patient)) {
            throw new UnauthorizedAccessException("Access denied: Only Patients can access this endpoint.");
        }
        return (Patient) user;
    }

    // ✅ View Patient Profile
    @GetMapping("/profile")
    public Patient getPatientProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return getAuthenticatedPatient(userDetails);
    }

    // ✅ Update Profile
    @PutMapping("/profile")
    public String updatePatientProfile(@RequestBody Patient patient, @AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        patientService.updatePatient(authenticatedPatient.getId(), patient);
        return "Patient profile updated successfully.";
    }

    // ✅ Soft Delete Account
    @DeleteMapping("/delete-account")
    public String deletePatientAccount(@AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        patientService.softDeletePatient(authenticatedPatient.getId());
        return "Patient account deleted successfully.";
    }

    // ✅ View Upcoming and Past Appointments
    @GetMapping("/appointments")
    public List<Appointment> getPatientAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        return appointmentService.getAppointmentsByPatient(authenticatedPatient.getId());
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam LocalDateTime appointmentTime,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        appointmentService.bookAppointment(authenticatedPatient.getId(), doctorId, appointmentTime);
        return "Appointment booked successfully.";
    }



    // ✅ Cancel an Appointment
    @DeleteMapping("/appointments/cancel")
    public String cancelAppointment(@RequestParam Long appointmentId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        appointmentService.cancelAppointment(authenticatedPatient.getId(), appointmentId);
        return "Appointment cancelled successfully.";
    }

    // ✅ View Prescriptions
    @GetMapping("/prescriptions")
    public List<?> getPatientPrescriptions(@AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        return patientService.getPrescriptions(authenticatedPatient.getId());
    }

    // ✅ Request a Prescription Refill
    @PostMapping("/prescriptions/request-refill")
    public String requestPrescriptionRefill(@RequestParam Long prescriptionId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        patientService.requestRefill(authenticatedPatient.getId(), prescriptionId);
        return "Prescription refill request submitted.";
    }

    // ✅ View Messages
    @GetMapping("/messages")
    public List<?> getPatientMessages(@AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        return patientService.getMessages(authenticatedPatient.getId());
    }

    // ✅ Send Message to Doctor
    @PostMapping("/messages/send")
    public String sendMessageToDoctor(@RequestParam Long doctorId,
                                      @RequestBody String message,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        Patient authenticatedPatient = getAuthenticatedPatient(userDetails);
        patientService.sendMessage(authenticatedPatient.getId(), doctorId, message);
        return "Message sent successfully.";
    }
}
