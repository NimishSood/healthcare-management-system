package com.example.healthcare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "Appointment Canceled", "Profile Updated"

    @Column(nullable = false)
    private String performedBy; // e.g., "admin@example.com"

    @Column(nullable = false)
    private String role; // e.g., "ADMIN", "DOCTOR", "PATIENT"

    @Column(nullable = false)
    private LocalDateTime timestamp; // When the action happened

    private String affectedEntity; // e.g., "Appointment ID: 23", "User ID: 45"

    private String previousData; // Optional: store before-change values

    private String newData; // Optional: store after-change values

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now(); // Automatically set timestamp
    }
}
