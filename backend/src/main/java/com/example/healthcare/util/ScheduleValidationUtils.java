// src/main/java/com/example/healthcare/util/ScheduleValidationUtils.java
package com.example.healthcare.util;

import java.time.*;

public class ScheduleValidationUtils {
    // For one-time slot: cannot add/edit/delete in the past (end time must be after now)
    public static boolean isOneTimeSlotPast(LocalDate date, LocalTime endTime) {
        if (date == null || endTime == null) return false;
        LocalDateTime slotEnd = LocalDateTime.of(date, endTime);
        return slotEnd.isBefore(LocalDateTime.now());
    }

    // For recurring: cannot add/update to a slot that would end before the current time (for today)
    public static boolean isRecurringPast(DayOfWeek dayOfWeek, LocalTime endTime) {
        if (dayOfWeek == null || endTime == null) return false;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        if (dayOfWeek.getValue() < today.getValue()) return true; // Already passed this week
        if (dayOfWeek.getValue() > today.getValue()) return false; // Later this week
        // Today: check endTime
        LocalTime now = LocalTime.now();
        return now.isAfter(endTime);
    }
}
