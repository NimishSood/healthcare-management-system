package com.example.healthcare.dto.DoctorSchedule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class OneTimeSlotDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available = true;
}
