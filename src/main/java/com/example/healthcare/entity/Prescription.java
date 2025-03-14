package com.example.healthcare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private boolean isRefillable;

    // Getters and setters
}
