package com.example.healthcare.dto.DoctorSchedule;

import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringSchedule;
import com.example.healthcare.entity.DoctorSchedule.DoctorOneTimeSlot;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringBreak;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DoctorFullScheduleDto {
    private List<DoctorRecurringSchedule> recurringSlots;
    private List<DoctorOneTimeSlot> oneTimeSlots;
    private List<DoctorRecurringBreak> recurringBreaks;
}
