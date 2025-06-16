package com.example.healthcare.controller;

import com.example.healthcare.dto.Appointments.AppointmentDto;
import com.example.healthcare.dto.Profiles.DoctorProfileDto;
import com.example.healthcare.dto.Profiles.ProfileMapper;
import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Prescription;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.healthcare.dto.Profiles.PatientProfileDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final SecurityUtils securityUtils;

    // ✅ View Doctor Profile
    @GetMapping("/profile")
    public DoctorProfileDto getDoctorProfile() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return ProfileMapper.toDoctorDto(doctor);
    }

    // ✅ Update Profile
    @PutMapping("/profile")
    public String updateDoctorProfile(@RequestBody Doctor updatedDoctor) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorService.updateDoctorProfile(doctor, updatedDoctor);
        return "Doctor profile updated successfully.";
    }

    // ✅ Soft Delete Account
    @DeleteMapping("/delete-account")
    public String deleteDoctor() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorService.softDeleteDoctor(doctor);
        return "Doctor account deleted successfully.";
    }

    // ✅ View Assigned Appointments (Upcoming)
    @GetMapping("/appointments/upcoming")
    public List<AppointmentDto> getUpcomingAppointments(@RequestParam(value = "limit", required = false) Integer limit) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        List<AppointmentDto> all = appointmentService.getUpcomingAppointmentsDto(doctor.getId(), true);
        if (limit != null && limit > 0) {
            return all.stream().limit(limit).collect(Collectors.toList());
        }
        return all;
    }

    // ✅ View Past Appointments
    @GetMapping("/appointments/history")
    public List<Appointment> getPastAppointments() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return appointmentService.getPastAppointments(doctor);
    }

    // ✅ Mark Appointment as Completed
    @PutMapping("/appointments/{appointmentId}/mark-complete")
    public String markAppointmentComplete(@PathVariable Long appointmentId) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        appointmentService.markAppointmentComplete(doctor.getId(), appointmentId);
        return "Appointment marked as completed.";
    }

    // ✅ View Messages with Patients
    @GetMapping("/messages")
    public List<?> getMessages() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.getMessages(doctor.getId());
    }

    // ✅ Send Message to a Patient
    @PostMapping("/send-message")
    public String sendMessage(@RequestParam Long patientId,
                              @RequestBody String message) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorService.sendMessage(doctor.getId(), patientId, message);
        return "Message sent successfully.";
    }

    // ✅ View Prescriptions Issued
    @GetMapping("/prescriptions")
    public List<Prescription >getPrescriptions() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.getPrescriptions(doctor.getId());
    }
    @GetMapping("/prescriptions/pending")
    public List<Prescription> getPendingRefillRequests() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.getPendingRefillRequests(doctor.getId());
    }

    // ✅ Issue a New Prescription
    @PostMapping("/issue-prescription")
    public String issuePrescription(@RequestParam Long patientId,
                                    @RequestParam String medicationDetails) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorService.issuePrescription(doctor, patientId, medicationDetails);
        return "Prescription issued successfully.";
    }

    @DeleteMapping("/appointments/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        appointmentService.cancelAppointment(doctor.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointments/cancelled")
    public List<AppointmentDto> getCancelledAppointments() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return appointmentService.getCancelledAppointmentsForDoctor(doctor.getId());
    }

    @GetMapping("/prescriptions/pending/count")
    public long countPendingPrescriptions() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.countPendingRefillRequests(doctor.getId());
    }

    @GetMapping("/messages/unread/count")
    public long countUnreadMessages() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.countUnreadMessages(doctor.getId());
    }

    @GetMapping("/patients")
    public List<PatientProfileDto> listPatients() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.getPatientsForDoctor(doctor.getId());
    }
    @GetMapping("/patients/{patientId}")
    public PatientProfileDto getPatient(@PathVariable Long patientId) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorService.getPatientProfile(doctor.getId(), patientId);
    }




}
