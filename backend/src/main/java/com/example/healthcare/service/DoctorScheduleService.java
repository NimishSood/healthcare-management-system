package com.example.healthcare.service;

// DoctorScheduleService.java
import com.example.healthcare.dto.DoctorSchedule.DoctorFullScheduleDto;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringSchedule;
import com.example.healthcare.entity.DoctorSchedule.DoctorOneTimeSlot;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringBreak;
import com.example.healthcare.repository.DoctorSchedule.DoctorRecurringScheduleRepository;
import com.example.healthcare.repository.DoctorSchedule.DoctorOneTimeSlotRepository;
import com.example.healthcare.repository.DoctorSchedule.DoctorRecurringBreakRepository;
import com.example.healthcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorRepository doctorRepository;
    private final DoctorRecurringScheduleRepository recurringRepo;
    private final DoctorOneTimeSlotRepository oneTimeRepo;
    private final DoctorRecurringBreakRepository breakRepo;

    // -- Recurring Schedule --
    public List<DoctorRecurringSchedule> getRecurringSchedule(Long doctorId) {
        return recurringRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorRecurringSchedule addRecurringSlot(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        // TODO: Add overlap validation logic
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringSchedule slot = new DoctorRecurringSchedule();
        slot.setDoctor(doctor);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        return recurringRepo.save(slot);
    }

    @Transactional
    public void deleteRecurringSlot(Long slotId) {
        recurringRepo.deleteById(slotId);
    }

    // -- One-time Slot --
    public List<DoctorOneTimeSlot> getOneTimeSlots(Long doctorId) {
        return oneTimeRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorOneTimeSlot addOneTimeSlot(Long doctorId, LocalDate date, LocalTime start, LocalTime end, boolean available) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorOneTimeSlot slot = new DoctorOneTimeSlot();
        slot.setDoctor(doctor);
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);
        return oneTimeRepo.save(slot);
    }

    @Transactional
    public void deleteOneTimeSlot(Long slotId) {
        oneTimeRepo.deleteById(slotId);
    }

    // -- Recurring Breaks --
    public List<DoctorRecurringBreak> getRecurringBreaks(Long doctorId) {
        return breakRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorRecurringBreak addRecurringBreak(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringBreak brk = new DoctorRecurringBreak();
        brk.setDoctor(doctor);
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);
        return breakRepo.save(brk);
    }

    @Transactional
    public void deleteRecurringBreak(Long breakId) {
        breakRepo.deleteById(breakId);
    }

    // --- Helper: Get full schedule for doctor ---
    public DoctorFullScheduleDto getFullSchedule(Long doctorId) {
        return new DoctorFullScheduleDto(
                getRecurringSchedule(doctorId),
                getOneTimeSlots(doctorId),
                getRecurringBreaks(doctorId)
        );
    }
}
