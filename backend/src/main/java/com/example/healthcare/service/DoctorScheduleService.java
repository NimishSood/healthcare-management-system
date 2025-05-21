package com.example.healthcare.service;

import com.example.healthcare.dto.DoctorSchedule.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService
{

    private final DoctorRepository doctorRepository;
    private final DoctorRecurringScheduleRepository recurringRepo;
    private final DoctorOneTimeSlotRepository oneTimeRepo;
    private final DoctorRecurringBreakRepository breakRepo;

    // --- Recurring Schedule CRUD ---

    public List<DoctorRecurringSchedule> getRecurringSchedule(Long doctorId) {
        return recurringRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorRecurringSchedule addRecurringSlot(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringSchedule slot = new DoctorRecurringSchedule();
        slot.setDoctor(doctor);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        // TODO: Check for overlaps with other recurring slots or breaks
        return recurringRepo.save(slot);
    }

    @Transactional
    public DoctorRecurringSchedule updateRecurringSlot(Long doctorId, Long slotId, DayOfWeek day, LocalTime start, LocalTime end) {
        DoctorRecurringSchedule slot = recurringRepo.findById(slotId).orElseThrow();
        // Only allow update if slot belongs to doctor
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        // TODO: Check for overlaps
        return recurringRepo.save(slot);
    }

    @Transactional
    public void deleteRecurringSlot(Long slotId) {
        recurringRepo.deleteById(slotId);
    }

    // --- One-time Slot CRUD ---

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
        // TODO: Check for overlaps with recurring slots or breaks
        return oneTimeRepo.save(slot);
    }

    @Transactional
    public DoctorOneTimeSlot updateOneTimeSlot(Long doctorId, Long slotId, LocalDate date, LocalTime start, LocalTime end, boolean available) {
        DoctorOneTimeSlot slot = oneTimeRepo.findById(slotId).orElseThrow();
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);
        // TODO: Overlap checks
        return oneTimeRepo.save(slot);
    }

    @Transactional
    public void deleteOneTimeSlot(Long slotId) {
        oneTimeRepo.deleteById(slotId);
    }

    // --- Recurring Breaks CRUD ---

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
        // TODO: Check for overlap with working slots
        return breakRepo.save(brk);
    }

    @Transactional
    public DoctorRecurringBreak updateRecurringBreak(Long doctorId, Long breakId, DayOfWeek day, LocalTime start, LocalTime end) {
        DoctorRecurringBreak brk = breakRepo.findById(breakId).orElseThrow();
        if (!Objects.equals(brk.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);
        // TODO: Overlap checks
        return breakRepo.save(brk);
    }

    @Transactional
    public void deleteRecurringBreak(Long breakId) {
        breakRepo.deleteById(breakId);
    }

    // --- Fetch Schedule for Range ---
    public DoctorFullScheduleDto getFullSchedule(Long doctorId) {
        return new DoctorFullScheduleDto(
                getRecurringSchedule(doctorId),
                getOneTimeSlots(doctorId),
                getRecurringBreaks(doctorId)
        );
    }

    public DoctorFullScheduleDto getScheduleForRange(Long doctorId, LocalDate start, LocalDate end) {
        // Filter one-time slots for range
        List<DoctorOneTimeSlot> oneTimeSlots = oneTimeRepo.findByDoctorIdAndDateBetween(doctorId, start, end);
        // Filter recurring slots and breaks for days of week present in range
        Set<DayOfWeek> rangeDays = start.datesUntil(end.plusDays(1))
                .map(LocalDate::getDayOfWeek).collect(Collectors.toSet());
        List<DoctorRecurringSchedule> recurringSlots = recurringRepo.findByDoctorId(doctorId).stream()
                .filter(slot -> rangeDays.contains(slot.getDayOfWeek()))
                .collect(Collectors.toList());
        List<DoctorRecurringBreak> recurringBreaks = breakRepo.findByDoctorId(doctorId).stream()
                .filter(brk -> rangeDays.contains(brk.getDayOfWeek()))
                .collect(Collectors.toList());
        return new DoctorFullScheduleDto(recurringSlots, oneTimeSlots, recurringBreaks);
    }

    // --- Batch Replace / Template Import ---

    @Transactional
    public void replaceWeek(Long doctorId, List<RecurringSlotDto> weekSchedule) {
        // Remove all existing recurring slots for this doctor
        recurringRepo.deleteByDoctorId(doctorId);
        // Add new ones
        for (RecurringSlotDto dto : weekSchedule) {
            addRecurringSlot(doctorId, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        }
    }

    @Transactional
    public DoctorFullScheduleDto importSchedule(Long doctorId, DoctorFullScheduleDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        // Remove all old slots/breaks (could also soft-delete for history)
        recurringRepo.deleteByDoctorId(doctorId);
        oneTimeRepo.deleteByDoctorId(doctorId);
        breakRepo.deleteByDoctorId(doctorId);

        // Save new Recurring Slots
        if (dto.getRecurringSlots() != null) {
            dto.getRecurringSlots().forEach(slot -> {
                slot.setDoctor(doctor);
                slot.setId(null); // ensure new
                recurringRepo.save(slot);
            });
        }

        // Save new One-Time Slots
        if (dto.getOneTimeSlots() != null) {
            dto.getOneTimeSlots().forEach(slot -> {
                slot.setDoctor(doctor);
                slot.setId(null); // ensure new
                oneTimeRepo.save(slot);
            });
        }

        // Save new Recurring Breaks
        if (dto.getRecurringBreaks() != null) {
            dto.getRecurringBreaks().forEach(brk -> {
                brk.setDoctor(doctor);
                brk.setId(null); // ensure new
                breakRepo.save(brk);
            });
        }

        // Return updated full schedule
        return getFullSchedule(doctorId);
    }

    // --- Check if slot is available (for booking) ---
    public boolean isAvailable(Long doctorId, LocalDate date, LocalTime start, LocalTime end) {
        // 1. Check one-time slots
        List<DoctorOneTimeSlot> daySlots = oneTimeRepo.findByDoctorIdAndDate(doctorId, date);
        for (DoctorOneTimeSlot slot : daySlots) {
            if (slot.isAvailable() && overlaps(slot.getStartTime(), slot.getEndTime(), start, end)) {
                return true;
            }
        }
        // 2. Check recurring slots and breaks
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<DoctorRecurringSchedule> recSlots = recurringRepo.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        List<DoctorRecurringBreak> recBreaks = breakRepo.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);

        boolean inWorking = recSlots.stream().anyMatch(slot ->
                overlaps(slot.getStartTime(), slot.getEndTime(), start, end)
        );
        boolean inBreak = recBreaks.stream().anyMatch(brk ->
                overlaps(brk.getStartTime(), brk.getEndTime(), start, end)
        );
        return inWorking && !inBreak;
    }

    // Helper: check time overlap
    private boolean overlaps(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    @Transactional
    public void importTemplate(Long doctorId, DoctorFullScheduleDto template) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Delete existing schedule for this doctor
        recurringRepo.deleteByDoctorId(doctorId);
        oneTimeRepo.deleteByDoctorId(doctorId);
        breakRepo.deleteByDoctorId(doctorId);

        // Import recurring slots
        if (template.getRecurringSlots() != null) {
            for (DoctorRecurringSchedule slot : template.getRecurringSlots()) {
                slot.setDoctor(doctor);
                slot.setId(null); // to force insert, not update
                recurringRepo.save(slot);
            }
        }

        // Import one-time slots
        if (template.getOneTimeSlots() != null) {
            for (DoctorOneTimeSlot slot : template.getOneTimeSlots()) {
                slot.setDoctor(doctor);
                slot.setId(null);
                oneTimeRepo.save(slot);
            }
        }

        // Import recurring breaks
        if (template.getRecurringBreaks() != null) {
            for (DoctorRecurringBreak brk : template.getRecurringBreaks()) {
                brk.setDoctor(doctor);
                brk.setId(null);
                breakRepo.save(brk);
            }
        }
    }

}
