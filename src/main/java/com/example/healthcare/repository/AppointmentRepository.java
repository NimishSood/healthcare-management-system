package com.example.healthcare.repository;

import com.example.healthcare.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByIsDeletedFalse();
    Optional<Appointment> findByIdAndIsDeletedFalse(Long id);

    // New method to check for overlapping appointments
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime startTime, LocalDateTime endTime);


    long countByAppointmentTimeAfter(LocalDateTime localDateTime);
}