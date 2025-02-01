package com.example.healthcare.service;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.exception.AppointmentNotFoundException;
import com.example.healthcare.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public Appointment bookAppointment(Appointment appointment) {
        // Check for overlapping appointments
        if (!isAppointmentAvailable(appointment.getDoctor().getId(), appointment.getAppointmentTime())) {
            throw new AppointmentConflictException("Appointment time conflicts with an existing appointment.");
        }

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

    public void updateAppointment(Long id, Appointment updated) {
        Appointment existing = getAppointment(id);
        if (updated.getAppointmentTime() != null) {
            existing.setAppointmentTime(updated.getAppointmentTime());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        appointmentRepository.save(existing);
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = getAppointment(id);
        appointment.setDeleted(true); // Soft deletion
        appointmentRepository.save(appointment);
    }
}