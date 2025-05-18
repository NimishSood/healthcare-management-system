package com.example.healthcare.repository;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
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

    // NEW: fetch cancelled (soft‐deleted) appointments for a patient
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    // Find all unique doctors a patient has had appointments with (non-deleted)
    @Query("SELECT DISTINCT a.doctor FROM Appointment a WHERE a.patient.id = :patientId AND a.isDeleted = false")
    List<User> findDoctorsByPatientId(Long patientId);

    // Find all unique patients a doctor has had appointments with (non-deleted)
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId AND a.isDeleted = false")
    List<User> findPatientsByDoctorId(Long doctorId);




}
