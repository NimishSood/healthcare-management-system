package com.example.healthcare.service;

import com.example.healthcare.dto.Appointments.AppointmentDto;
import com.example.healthcare.dto.Appointments.AppointmentMapper;
import com.example.healthcare.entity.*;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentNotFoundException;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository    doctorRepository;
    private final PatientRepository   patientRepository;
    private final UserRepository      userRepository;
    private final AuditLogService     auditLogService; // ✅ Injected Audit Log Service
    private final DoctorScheduleService doctorScheduleService;

    // ✅ Patient Books an Appointment
    @Transactional
    public void bookAppointment(Long patientId, Long doctorId, LocalDateTime appointmentTime) {
        // --- Fetch and validate patient & doctor ---
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        // --- Ensure appointment time is in the future ---
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("You cannot book an appointment in the past.");
        }

        // --- Prevent double-booking ---
        if (appointmentRepository.existsByDoctorIdAndAppointmentTime(doctorId, appointmentTime)) {
            throw new IllegalArgumentException("This time slot is already booked.");
        }

        // --- Verify the doctor is working at this time ---
        if (!doctorScheduleService.isAppointmentTimeAvailable(doctorId, appointmentTime)) {
            throw new IllegalArgumentException("Doctor is not available at the requested time.");
        }


        // --- Create and populate appointment entity ---
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        // ✨ **IMPORTANT FIX** ✨
        // Provide non-null defaults for columns defined NOT NULL in the database.
        // Otherwise, MySQL will throw a constraint violation if they remain null.
        appointment.setLocation(""); // empty string instead of null
        appointment.setNotes("");    // empty string instead of null

        // --- Persist to DB ---
        appointmentRepository.save(appointment);

        // --- Audit log ---
        auditLogService.logAction(
                "Appointment Booked",
                patient.getEmail(),
                "PATIENT",
                "Doctor ID: " + doctorId,
                null,
                "Appointment Time: " + appointmentTime
        );
    }

    // ✅ Cancel Appointment (Patients & Admins)
    @Transactional
    public void cancelAppointment(Long userId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found."));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Completed appointments cannot be canceled.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Only patient or doctor can cancel
        if (appointment.getPatient().getId().equals(userId) ||
                appointment.getDoctor().getId().equals(userId)) {

            String previousData = appointment.toString();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setCancelledBy(user);
            appointmentRepository.save(appointment);

            auditLogService.logAction(
                    "Appointment Cancelled",
                    user.getEmail(),
                    user.getRole().name(),
                    "Appointment ID: " + appointmentId,
                    previousData,
                    "Status: CANCELLED"
            );
        } else {
            throw new UnauthorizedAccessException(
                    "Only the assigned doctor or patient can cancel this appointment.");
        }
    }

    // ✅ Get Upcoming Appointments (For Patients & Doctors)
    public List<Appointment> getUpcomingAppointments(Long userId, boolean isDoctor) {
        return isDoctor
                ? appointmentRepository.findByDoctorIdAndAppointmentTimeAfter(userId, LocalDateTime.now())
                : appointmentRepository.findByPatientIdAndAppointmentTimeAfter(userId, LocalDateTime.now());
    }

    // ✅ Get Past Appointments (For Patients & Doctors)
    public List<Appointment> getPastAppointments(Long userId, boolean isDoctor) {
        return isDoctor
                ? appointmentRepository.findByDoctorIdAndAppointmentTimeBefore(userId, LocalDateTime.now())
                : appointmentRepository.findByPatientIdAndAppointmentTimeBefore(userId, LocalDateTime.now());
    }

    // ✅ Mark Appointment as Completed (Doctors Only)
    @Transactional
    public void markAppointmentComplete(Long doctorId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found."));

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException(
                    "Doctors can only complete their own appointments.");
        }

        if (appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("You cannot complete a future appointment.");
        }

        String previousData = appointment.toString();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        auditLogService.logAction(
                "Appointment Completed",
                appointment.getDoctor().getEmail(),
                "DOCTOR",
                "Appointment ID: " + appointmentId,
                previousData,
                "Status: COMPLETED"
        );
    }

    // ✅ Other methods unchanged...
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    public List<Appointment> getUpcomingAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdAndAppointmentTimeAfter(patientId, LocalDateTime.now());
    }
    public List<Appointment> getPastAppointmentsByPatient(Long patientId) {
        List<Appointment> pastAppointments =
                appointmentRepository.findByPatientIdAndAppointmentTimeBefore(patientId, LocalDateTime.now());
        for (Appointment appt : pastAppointments) {
            if (appt.getStatus() == AppointmentStatus.BOOKED
                    && appt.getAppointmentTime().isBefore(LocalDateTime.now())) {
                appt.setStatus(AppointmentStatus.COMPLETED);
                appointmentRepository.save(appt);
            }
        }
        return pastAppointments;
    }
    public List<Appointment> getUpcomingAppointments(User doctor) {
        return appointmentRepository.findByDoctorIdAndAppointmentTimeAfter(doctor.getId(), LocalDateTime.now());
    }
    public List<Appointment> getPastAppointments(User doctor) {
        return appointmentRepository.findByDoctorIdAndAppointmentTimeBefore(doctor.getId(), LocalDateTime.now());
    }
    public List<AppointmentDto> getUpcomingAppointmentsDto(Long userId, boolean isDoctor) {
        return appointmentRepository
                .findByPatientIdAndAppointmentTimeAfter(userId, LocalDateTime.now())
                .stream()
                .filter(a -> !a.isDeleted() && a.getStatus() != AppointmentStatus.CANCELLED)  // ← drop cancelled here
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<AppointmentDto> getPastAppointmentsDto(Long userId, boolean isDoctor) {
        return appointmentRepository
                .findByPatientIdAndAppointmentTimeBefore(userId, LocalDateTime.now())
                .stream()
                .filter(a -> !a.isDeleted() && a.getStatus() != AppointmentStatus.CANCELLED)  // ← and here
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getCancelledAppointmentsDto(Long patientId) {
        return appointmentRepository
                .findByPatientIdAndStatus(patientId, AppointmentStatus.CANCELLED)
                .stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }
}
