package com.example.healthcare.service;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.exception.AppointmentNotFoundException;
import com.example.healthcare.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        // Validate appointment
        if (appointment == null || appointment.getDoctor() == null || appointment.getPatient() == null || appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment, doctor, patient, and appointment time must not be null.");
        }

        // Check for overlapping appointments
        if (!isAppointmentAvailable(appointment.getDoctor().getId(), appointment.getAppointmentTime())) {
            throw new AppointmentConflictException("Appointment time conflicts with an existing appointment.");
        }

        // Set status and save
        appointment.setStatus(AppointmentStatus.BOOKED);
        return appointmentRepository.save(appointment);
    }

    public boolean isAppointmentAvailable(Long doctorId, LocalDateTime appointmentTime) {
        // Define a time range for the appointment (e.g., 30 minutes)
        LocalDateTime startTime = appointmentTime.minusMinutes(30);
        LocalDateTime endTime = appointmentTime.plusMinutes(30);

        // Check for overlapping appointments
        List<Appointment> overlappingAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startTime, endTime);

        return overlappingAppointments.isEmpty();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByIsDeletedFalse();
    }

    public Appointment getAppointment(Long id) {
        return appointmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
    }

    @Transactional
    public void updateAppointment(Long id, Appointment updated) {
        Appointment existing = getAppointment(id);

        // Update appointment time (if provided) and check for conflicts
        if (updated.getAppointmentTime() != null) {
            if (!isAppointmentAvailable(existing.getDoctor().getId(), updated.getAppointmentTime())) {
                throw new AppointmentConflictException("Updated appointment time conflicts with an existing appointment.");
            }
            existing.setAppointmentTime(updated.getAppointmentTime());
        }

        // Update status (if provided)
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        // Save the updated appointment
        appointmentRepository.save(existing);
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = getAppointment(id);
        appointment.setDeleted(true); // Soft deletion
        appointment.setStatus(AppointmentStatus.CANCELLED); // Update status
        appointmentRepository.save(appointment);
    }
}