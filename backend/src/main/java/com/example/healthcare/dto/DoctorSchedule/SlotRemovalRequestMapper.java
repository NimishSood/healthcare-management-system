package com.example.healthcare.dto.DoctorSchedule;

import com.example.healthcare.dto.Profiles.ProfileMapper;
import com.example.healthcare.entity.DoctorSchedule.SlotRemovalRequest;

public class SlotRemovalRequestMapper
{
    public static SlotRemovalRequestDto toDto(SlotRemovalRequest req) {
        SlotRemovalRequestDto dto = new SlotRemovalRequestDto();
        dto.setId(req.getId());
        dto.setSlotType(req.getSlotType());
        dto.setSlotId(req.getSlotId());
        dto.setDoctor(ProfileMapper.toDoctorDto(req.getDoctor())); // This line does the mapping
        dto.setReason(req.getReason());
        dto.setStatus(req.getStatus());
        dto.setRequestedAt(req.getRequestedAt());
        dto.setReviewedAt(req.getReviewedAt());
        dto.setReviewedByAdminId(req.getReviewedByAdminId());
        dto.setAdminNote(req.getAdminNote());
        return dto;
    }

}
