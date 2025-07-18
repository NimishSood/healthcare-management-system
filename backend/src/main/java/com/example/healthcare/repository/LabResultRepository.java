package com.example.healthcare.repository;

import com.example.healthcare.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    List<LabResult> findByPatientId(Long patientId);
    List<LabResult> findByDoctorId(Long doctorId);
}