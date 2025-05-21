// DoctorScheduleController.java
package com.example.healthcare.controller;

import com.example.healthcare.dto.DoctorSchedule.*;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringSchedule;
import com.example.healthcare.entity.DoctorSchedule.DoctorOneTimeSlot;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringBreak;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/doctor/schedule")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public DoctorFullScheduleDto getSchedule() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.getFullSchedule(doctor.getId());
    }

    // Recurring Schedule
    @PostMapping("/recurring")
    public DoctorRecurringSchedule addRecurringSlot(@RequestBody RecurringSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addRecurringSlot(doctor.getId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @DeleteMapping("/recurring/{id}")
    public void deleteRecurringSlot(@PathVariable Long id) {
        doctorScheduleService.deleteRecurringSlot(id);
    }

    // One-Time Slot
    @PostMapping("/onetime")
    public DoctorOneTimeSlot addOneTimeSlot(@RequestBody OneTimeSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addOneTimeSlot(doctor.getId(), dto.getDate(), dto.getStartTime(), dto.getEndTime(), dto.isAvailable());
    }

    @DeleteMapping("/onetime/{id}")
    public void deleteOneTimeSlot(@PathVariable Long id) {
        doctorScheduleService.deleteOneTimeSlot(id);
    }

    // Recurring Breaks
    @PostMapping("/break")
    public DoctorRecurringBreak addRecurringBreak(@RequestBody RecurringBreakDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addRecurringBreak(doctor.getId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @DeleteMapping("/break/{id}")
    public void deleteRecurringBreak(@PathVariable Long id) {
        doctorScheduleService.deleteRecurringBreak(id);
    }
}
