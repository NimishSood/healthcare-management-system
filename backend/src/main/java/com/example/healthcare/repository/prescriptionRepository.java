package com.example.healthcare.repository;

import com.example.healthcare.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface prescriptionRepository extends JpaRepository<Prescription,Long>
{
}
