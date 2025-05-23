package com.example.healthcare.dto.DoctorSchedule;

import lombok.Data;

@Data
public class SlotRemovalRequestCreateDto {
    private String slotType; // "RECURRING", "ONE_TIME", "BREAK"
    private Long slotId;
    private String reason;
}
