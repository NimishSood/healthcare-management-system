package com.example.healthcare.controller;

import com.example.healthcare.dto.Appointments.AppointmentDto;
import com.example.healthcare.dto.Profiles.ChangePasswordRequest;
import com.example.healthcare.dto.Profiles.DoctorProfileDto;
import com.example.healthcare.dto.Profiles.PatientProfileDto;
import com.example.healthcare.dto.Profiles.ProfileMapper;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final SecurityUtils securityUtils;
    private final DoctorService doctorService;

    /**
     * GET /patient/profile
     * Returns the authenticated patient's profile information.
     */
    @GetMapping("/profile")
    public PatientProfileDto getPatientProfile() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return ProfileMapper.toPatientDto(patient);
    }

    /**
     * PUT /patient/profile
     * Updates the authenticated patient's profile.
     */
    @PutMapping("/profile")
    public String updatePatientProfile(@RequestBody Patient updated) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        patientService.updatePatient(patient.getId(), updated);
        return "Patient profile updated successfully.";
    }

    /**
     * DELETE /patient/delete-account
     * Soft-deletes the authenticated patient's account.
     */
    @DeleteMapping("/delete-account")
    public String deletePatientAccount() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        patientService.softDeletePatient(patient.getId());
        return "Patient account deleted successfully.";
    }

    /**
     * POST /patient/change-password
     * Changes password for the authenticated patient.
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        patientService.changePassword(
                patient.getId(),
                req.getOldPassword(),
                req.getNewPassword()
        );
        return ResponseEntity.ok("Password changed successfully.");
    }

    /**
     * POST /patient/appointments/book
     * Books a new appointment for the authenticated patient.
     *
     * @param doctorId        ID of the doctor to book with
     * @param appointmentTime Appointment time as a LocalDateTime
     */
    @PostMapping("/appointments/book")
    public String bookAppointment(
            @RequestParam Long doctorId,
            @RequestParam LocalDateTime appointmentTime
    ) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        appointmentService.bookAppointment(patient.getId(), doctorId, appointmentTime);
        return "Appointment booked successfully.";
    }

    /**
     * DELETE /patient/appointments/cancel
     * Cancels an existing appointment.
     *
     * @param appointmentId ID of the appointment to cancel
     */

    @DeleteMapping("/appointments/cancel")
    public ResponseEntity<Void> cancelAppointment(@RequestParam Long appointmentId) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        appointmentService.cancelAppointment(patient.getId(), appointmentId);
        // Return 204 No Content so the UI knows it succeeded without payload
        return ResponseEntity.noContent().build();
    }


    /**
     * GET /patient/prescriptions
     * Returns all prescriptions for the authenticated patient.
     */
    @GetMapping("/prescriptions")
    public List<?> getPatientPrescriptions() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return patientService.getPrescriptions(patient.getId());
    }

    /**
     * POST /patient/prescriptions/request-refill
     * Requests a refill for a given prescription.
     *
     * @param prescriptionId ID of the prescription
     */
    @PostMapping("/prescriptions/request-refill")
    public String requestPrescriptionRefill(@RequestParam Long prescriptionId) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        patientService.requestRefill(patient.getId(), prescriptionId);
        return "Prescription refill request submitted.";
    }

    /**
     * GET /patient/messages
     * Returns all messages for the authenticated patient.
     */
    @GetMapping("/messages")
    public List<?> getPatientMessages() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return patientService.getMessages(patient.getId());
    }

    /**
     * POST /patient/messages/send
     * Sends a message from the authenticated patient to a doctor.
     *
     * @param doctorId ID of the doctor
     * @param message  Message text in the request body
     */
    @PostMapping("/messages/send")
    public String sendMessageToDoctor(
            @RequestParam Long doctorId,
            @RequestBody String message
    ) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        patientService.sendMessage(patient.getId(), doctorId, message);
        return "Message sent successfully.";
    }

    /**
     * GET /patient/appointments/upcoming?limit={n}
     * Returns upcoming appointments, optionally limited to the first n.
     */
    @GetMapping("/appointments/upcoming")
    public List<AppointmentDto> getUpcomingAppointments(
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        List<AppointmentDto> all = appointmentService
                .getUpcomingAppointmentsDto(patient.getId(), false);

        if (limit != null && limit > 0) {
            return all.stream().limit(limit).collect(Collectors.toList());
        }
        return all;
    }

    /**
     * GET /patient/appointments/history?since={ISO_DATE_TIME}
     * Returns past appointments, optionally filtering from a given timestamp.
     */
    @GetMapping("/appointments/history")
    public List<AppointmentDto> getPastAppointments(
            @RequestParam(value = "since", required = false) String since
    ) {
        Patient patient = securityUtils.getAuthenticatedPatient();
        List<AppointmentDto> all = appointmentService
                .getPastAppointmentsDto(patient.getId(), false);

        if (since != null) {
            LocalDateTime cutoff = LocalDateTime.parse(since);
            return all.stream()
                    .filter(a -> a.getAppointmentTime().isAfter(cutoff))
                    .collect(Collectors.toList());
        }
        return all;
    }

    /**
     * GET /patient/prescriptions/pending/count
     * Returns the number of pending prescription refills for the authenticated patient.
     */
    @GetMapping("/prescriptions/pending/count")
    public long countPendingRefills() {
        Patient patient = securityUtils.getAuthenticatedPatient();
        return patientService.countPendingRefills(patient.getId());
    }

    /**
     * GET /patient/doctors
     * Returns a list of all active doctors (profile DTOs).
     */
    @GetMapping("/doctors")
    public List<DoctorProfileDto> listDoctors() {
        return doctorService.getAllDoctors().stream()
                .map(ProfileMapper::toDoctorDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/appointments/cancelled")
    public List<AppointmentDto> getCancelledAppointments() {
        Patient p = securityUtils.getAuthenticatedPatient();
        return appointmentService.getCancelledAppointmentsDto(p.getId());
    }
}
