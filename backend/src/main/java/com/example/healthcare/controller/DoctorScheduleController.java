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

    // ========= CORE: Get All Schedule Data =========

    // Get full schedule for calendar rendering (all recurring, one-time, breaks, plus appointments if needed)
    @GetMapping("/full")
    public DoctorFullScheduleDto getFullSchedule() {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.getFullSchedule(doctor.getId());
    }

    // Get schedule for a given date range (e.g., for week/month view)
    @GetMapping
    public DoctorFullScheduleDto getScheduleForRange(
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end
    ) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.getScheduleForRange(doctor.getId(), start, end);
    }

    // ========= RECURRING SCHEDULE CRUD =========

    @PostMapping("/recurring")
    public DoctorRecurringSchedule addRecurringSlot(@RequestBody RecurringSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addRecurringSlot(doctor.getId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @PutMapping("/recurring/{id}")
    public DoctorRecurringSchedule updateRecurringSlot(@PathVariable Long id, @RequestBody RecurringSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.updateRecurringSlot(doctor.getId(), id, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @DeleteMapping("/recurring/{id}")
    public void deleteRecurringSlot(@PathVariable Long id) {
        doctorScheduleService.deleteRecurringSlot(id);
    }

    // ========= ONE-TIME SLOT CRUD =========

    @PostMapping("/onetime")
    public DoctorOneTimeSlot addOneTimeSlot(@RequestBody OneTimeSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addOneTimeSlot(doctor.getId(), dto.getDate(), dto.getStartTime(), dto.getEndTime(), dto.isAvailable());
    }

    @PutMapping("/onetime/{id}")
    public DoctorOneTimeSlot updateOneTimeSlot(@PathVariable Long id, @RequestBody OneTimeSlotDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.updateOneTimeSlot(doctor.getId(), id, dto.getDate(), dto.getStartTime(), dto.getEndTime(), dto.isAvailable());
    }

    @DeleteMapping("/onetime/{id}")
    public void deleteOneTimeSlot(@PathVariable Long id) {
        doctorScheduleService.deleteOneTimeSlot(id);
    }

    // ========= RECURRING BREAKS CRUD =========

    @PostMapping("/break")
    public DoctorRecurringBreak addRecurringBreak(@RequestBody RecurringBreakDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.addRecurringBreak(doctor.getId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @PutMapping("/break/{id}")
    public DoctorRecurringBreak updateRecurringBreak(@PathVariable Long id, @RequestBody RecurringBreakDto dto) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.updateRecurringBreak(doctor.getId(), id, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
    }

    @DeleteMapping("/break/{id}")
    public void deleteRecurringBreak(@PathVariable Long id) {
        doctorScheduleService.deleteRecurringBreak(id);
    }

    // ========= BULK/BATCH ACTIONS (Optional, Professional) =========

    // Replace the whole week with new recurring slots
    @PutMapping("/replace-week")
    public void replaceWeek(@RequestBody List<RecurringSlotDto> weekSchedule) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorScheduleService.replaceWeek(doctor.getId(), weekSchedule);
    }

    // Import a schedule template (e.g., for copying previous weeks or holidays)
    @PostMapping("/import-template")
    public void importTemplate(@RequestBody DoctorFullScheduleDto template) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        doctorScheduleService.importTemplate(doctor.getId(), template);
    }

    // ========= AVAILABILITY/CONFLICT CHECK (Nice to have) =========

    // Check if a time slot is available (returns true/false)
    @GetMapping("/availability")
    public boolean isAvailable(
            @RequestParam("date") LocalDate date,
            @RequestParam("start") LocalTime start,
            @RequestParam("end") LocalTime end
    ) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        return doctorScheduleService.isAvailable(doctor.getId(), date, start, end);
    }
}
