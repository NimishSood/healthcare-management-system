package com.example.healthcare.repository;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ✅ Find all upcoming appointments for a specific patient
    List<Appointment> findByPatientIdAndAppointmentTimeAfter(Long patientId, LocalDateTime now);

    // ✅ Find all past appointments for a specific patient
    List<Appointment> findByPatientIdAndAppointmentTimeBefore(Long patientId, LocalDateTime now);

    // ✅ Find all upcoming appointments for a specific doctor
    List<Appointment> findByDoctorIdAndAppointmentTimeAfter(Long doctorId, LocalDateTime now);

    // ✅ Find all past appointments for a specific doctor
    List<Appointment> findByDoctorIdAndAppointmentTimeBefore(Long doctorId, LocalDateTime now);

    // ✅ Find an appointment by ID (Ensure it exists)
    Optional<Appointment> findById(Long id);

    // ✅ Find appointments by status (BOOKED, COMPLETED, CANCELLED)
    List<Appointment> findByStatus(AppointmentStatus status);

    // ✅ Check if an appointment slot is available for a doctor
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    List<Appointment> findByDoctorAndAppointmentTimeBeforeAndIsDeletedFalse(Doctor doctor, LocalDateTime now);

    List<Appointment> findByDoctorAndAppointmentTimeAfterAndIsDeletedFalse(Doctor doctor, LocalDateTime now);

    List<Appointment> findByPatientId(Long patientId);
}
