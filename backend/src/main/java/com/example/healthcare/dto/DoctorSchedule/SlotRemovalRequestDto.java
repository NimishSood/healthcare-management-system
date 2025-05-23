package com.example.healthcare.dto.DoctorSchedule;

import lombok.Data;
import java.time.LocalDateTime;
import com.example.healthcare.dto.Profiles.DoctorProfileDto;
@Data
public class SlotRemovalRequestDto {
    private Long id;
    private String slotType;
    private Long slotId;
    private DoctorProfileDto doctor; // Use your profile DTO
    private String reason;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedByAdminId;
    private String adminNote;
}
