package com.example.healthcare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private String testName;

    private String resultValue;

    private String units;

    private String normalRange;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime resultDate;

    @PrePersist
    public void prePersist() {
        if (resultDate == null) {
            resultDate = LocalDateTime.now();
        }
    }
}