package com.example.healthcare.repository;

import com.example.healthcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findAllByIsDeletedFalse();
    Optional<Patient> findByIdAndIsDeletedFalse(Long id); // Return Optional<Patient>
}