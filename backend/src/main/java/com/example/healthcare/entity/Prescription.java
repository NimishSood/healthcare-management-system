package com.example.healthcare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "prescriptions")
@Getter @Setter
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;             // The patient who the prescription is for

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;              // The doctor who issued the prescription

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;    // (Optional) Appointment during which it was prescribed

    @Column(nullable = false)
    private String medicationName;      // Name of the medication

    @Column(nullable = false)
    private String dosage;              // Dosage information (e.g. "500 mg twice daily")

    @Column(nullable = false)
    private String instructions;        // Usage instructions for the patient

    @Column(nullable = false)
    private int refillsLeft;            // How many refills remain (0 if none)

    @Column(nullable = false)
    private LocalDate dateIssued;       // Date the prescription was issued

    @Column(nullable = false)
    private boolean isDeleted = false;  // Soft-delete flag for canceled prescriptions

    @ManyToOne
    @JoinColumn(name = "cancelled_by", nullable = true)
    private User cancelledBy;           // User who cancelled (if isDeleted is true)
}

