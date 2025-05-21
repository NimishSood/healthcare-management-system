package com.example.healthcare.dto.DoctorSchedule;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class RecurringSlotDto {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
