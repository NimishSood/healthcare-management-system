package com.example.healthcare.repository;

import com.example.healthcare.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long>
{
    Optional<Prescription> findByIdAndIsDeletedFalse(Long id);
    List<Prescription> findByPatientIdAndIsDeletedFalse(Long patientId);
    List<Prescription> findByDoctorIdAndIsDeletedFalse(Long doctorId);
    long countByDoctorIdAndRefillRequestedTrueAndRefillStatus(Long doctorId, String refillStatus);
}
