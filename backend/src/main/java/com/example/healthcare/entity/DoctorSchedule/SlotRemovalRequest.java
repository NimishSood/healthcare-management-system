package com.example.healthcare.entity.DoctorSchedule;

import com.example.healthcare.entity.Doctor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "slot_removal_requests")
public class SlotRemovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who is making the request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Type of slot: "RECURRING", "ONE_TIME", "BREAK"
    @Column(nullable = false)
    private String slotType;

    // Slot ID in its table
    @Column(nullable = false)
    private Long slotId;

    // Doctor's explanation
    @Column(length = 1000)
    private String reason;

    // PENDING, APPROVED, REJECTED, or optionally PROCESSED
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    // Admin action
    private LocalDateTime reviewedAt;
    private Long reviewedByAdminId;
    private String adminNote;

    // Convenience getters for DTO mapping (optional)
    public Long getDoctorId() {
        return doctor != null ? doctor.getId() : null;
    }

    public String getDoctorName() {
        return doctor != null ? doctor.getFullName() : null;
    }

    // No setters for DoctorId/Name needed!
}
