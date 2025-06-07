// src/main/java/com/example/healthcare/util/ScheduleValidationUtils.java
package com.example.healthcare.util;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class ScheduleValidationUtils {
    // For one-time slot: cannot add/edit/delete in the past (end time must be after now)
    public static boolean isOneTimeSlotPast(LocalDate date, LocalTime endTime) {
        if (date == null || endTime == null) return false;
        LocalDateTime slotEnd = LocalDateTime.of(date, endTime);
        return slotEnd.isBefore(LocalDateTime.now());
    }

    // For recurring: check the next occurrence of the dayOfWeek. If that
    // occurrence's end time is before now, the slot is considered past.
    public static boolean isRecurringPast(DayOfWeek dayOfWeek, LocalTime endTime) {
        if (dayOfWeek == null || endTime == null) return false;
        LocalDate today = LocalDate.now();
        LocalDate next = today.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        LocalDateTime endDateTime = LocalDateTime.of(next, endTime);
        return endDateTime.isBefore(LocalDateTime.now());
    }


    public static boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        // Example: slot1 = [10:00, 11:00), slot2 = [11:00, 12:00) should NOT overlap
        return !start1.isAfter(end2) && !start2.isAfter(end1) && start1.isBefore(end2) && start2.isBefore(end1);
        // Simpler: return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
