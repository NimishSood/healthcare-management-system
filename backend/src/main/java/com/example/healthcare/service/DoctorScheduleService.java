package com.example.healthcare.service;

import com.example.healthcare.dto.DoctorSchedule.*;
import com.example.healthcare.dto.Profiles.DoctorProfileDto;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.DoctorSchedule.*;
import com.example.healthcare.repository.DoctorSchedule.*;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.AuditLogService;
import com.example.healthcare.util.ScheduleValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorRepository doctorRepository;
    private final DoctorRecurringScheduleRepository recurringRepo;
    private final DoctorOneTimeSlotRepository oneTimeRepo;
    private final DoctorRecurringBreakRepository breakRepo;
    private final SlotRemovalRequestRepository slotRemovalRequestRepository;
    private final AuditLogService auditLogService;

    // === Availability Check for Overlaps (Used in All Add/Update Ops) ===
    /**
     * Checks for any slot or break that overlaps with the desired slot time.
     * - For recurring: checks same day recurring slots & breaks.
     * - For one-time: checks same date one-time slots & recurring for the day.
     * - excludeId: skip an existing slot/break ID (for update ops).
     * - type: "RECURRING", "ONE_TIME", "BREAK"
     */
    public boolean isAvailable(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end, Long excludeId, String type) {
        // Check recurring slots
        for (DoctorRecurringSchedule slot : recurringRepo.findByDoctorIdAndActiveTrue(doctorId)) {
            if (!slot.getDayOfWeek().equals(day)) continue;
            if (excludeId != null && slot.getId().equals(excludeId) && "RECURRING".equals(type)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, slot.getStartTime(), slot.getEndTime())) return false;
        }
        // Check recurring breaks
        for (DoctorRecurringBreak brk : breakRepo.findByDoctorIdAndActiveTrue(doctorId)) {
            if (!brk.getDayOfWeek().equals(day)) continue;
            if (excludeId != null && brk.getId().equals(excludeId) && "BREAK".equals(type)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, brk.getStartTime(), brk.getEndTime())) return false;
        }
        // Check one-time slots ON THAT DAY
        for (DoctorOneTimeSlot slot : oneTimeRepo.findByDoctorId(doctorId)) {
            if (!slot.getDate().getDayOfWeek().equals(day)) continue;
            if (excludeId != null && slot.getId().equals(excludeId) && "ONE_TIME".equals(type)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, slot.getStartTime(), slot.getEndTime())) return false;
        }
        return true;
    }

    /**
     * For one-time slot: Also checks recurring slots/breaks for that day of week.
     */
    public boolean isAvailableOneTime(Long doctorId, LocalDate date, LocalTime start, LocalTime end, Long excludeId) {
        // 1. Check one-time slots for that date
        for (DoctorOneTimeSlot slot : oneTimeRepo.findByDoctorId(doctorId)) {
            if (!slot.getDate().equals(date)) continue;
            if (excludeId != null && slot.getId().equals(excludeId)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, slot.getStartTime(), slot.getEndTime())) return false;
        }
        // 2. Check recurring slots/breaks for that day of week
        DayOfWeek day = date.getDayOfWeek();
        // Recurring slots
        for (DoctorRecurringSchedule slot : recurringRepo.findByDoctorIdAndActiveTrue(doctorId)) {
            if (!slot.getDayOfWeek().equals(day)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, slot.getStartTime(), slot.getEndTime())) return false;
        }
        // Recurring breaks
        for (DoctorRecurringBreak brk : breakRepo.findByDoctorIdAndActiveTrue(doctorId)) {
            if (!brk.getDayOfWeek().equals(day)) continue;
            if (ScheduleValidationUtils.isTimeOverlap(start, end, brk.getStartTime(), brk.getEndTime())) return false;
        }
        return true;
    }


    // --- Recurring Schedule CRUD ---

    public List<DoctorRecurringSchedule> getRecurringSchedule(Long doctorId) {
        return recurringRepo.findByDoctorIdAndActiveTrue(doctorId);
    }

    @Transactional
    public DoctorRecurringSchedule addRecurringSlot(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot add a recurring slot in the past.");
        }
        // Overlap check:
        if (!isAvailable(doctorId, day, start, end, null, "RECURRING")) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringSchedule slot = new DoctorRecurringSchedule();
        slot.setDoctor(doctor);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);

        DoctorRecurringSchedule saved = recurringRepo.save(slot);

        // Audit log
        auditLogService.logAction(
                "Recurring Slot Added",
                doctor.getEmail(),
                doctor.getRole().name(),
                String.format("Added recurring slot: %s %s-%s", day, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public DoctorRecurringSchedule updateRecurringSlot(Long doctorId, Long slotId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot update a recurring slot in the past.");
        }
        // Overlap check (exclude self)
        if (!isAvailable(doctorId, day, start, end, slotId, "RECURRING")) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        DoctorRecurringSchedule slot = recurringRepo.findById(slotId).orElseThrow();
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);

        DoctorRecurringSchedule saved = recurringRepo.save(slot);

        // Audit log
        auditLogService.logAction(
                "Recurring Slot Updated",
                slot.getDoctor().getEmail(),
                slot.getDoctor().getRole().name(),
                String.format("Updated recurring slot ID %d to: %s %s-%s", slotId, day, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public void deleteRecurringSlot(Long slotId) {
        DoctorRecurringSchedule slot = recurringRepo.findById(slotId).orElseThrow();
        if (ScheduleValidationUtils.isRecurringPast(slot.getDayOfWeek(), slot.getEndTime())) {
            throw new IllegalArgumentException("Cannot delete a recurring slot in the past.");
        }
        recurringRepo.deleteById(slotId);

        // Audit log
        auditLogService.logAction(
                "Recurring Slot Deleted",
                slot.getDoctor().getEmail(),
                slot.getDoctor().getRole().name(),
                String.format("Deleted recurring slot ID %d", slotId),
                null,
                slot.toString()
        );
    }

    // --- One-time Slot CRUD ---

    public List<DoctorOneTimeSlot> getOneTimeSlots(Long doctorId) {
        return oneTimeRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorOneTimeSlot addOneTimeSlot(Long doctorId, LocalDate date, LocalTime start, LocalTime end, boolean available) {
        if (ScheduleValidationUtils.isOneTimeSlotPast(date, end)) {
            throw new IllegalArgumentException("Cannot add a one-time slot in the past.");
        }
        // Overlap check:
        if (!isAvailableOneTime(doctorId, date, start, end, null)) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorOneTimeSlot slot = new DoctorOneTimeSlot();
        slot.setDoctor(doctor);
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);

        DoctorOneTimeSlot saved = oneTimeRepo.save(slot);

        // Audit log
        auditLogService.logAction(
                "One-Time Slot Added",
                doctor.getEmail(),
                doctor.getRole().name(),
                String.format("Added one-time slot: %s %s-%s", date, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public DoctorOneTimeSlot updateOneTimeSlot(Long doctorId, Long slotId, LocalDate date, LocalTime start, LocalTime end, boolean available) {
        if (ScheduleValidationUtils.isOneTimeSlotPast(date, end)) {
            throw new IllegalArgumentException("Cannot edit a one-time slot in the past.");
        }
        // Overlap check (exclude self)
        if (!isAvailableOneTime(doctorId, date, start, end, slotId)) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        DoctorOneTimeSlot slot = oneTimeRepo.findById(slotId).orElseThrow();
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);

        DoctorOneTimeSlot saved = oneTimeRepo.save(slot);

        // Audit log
        auditLogService.logAction(
                "One-Time Slot Updated",
                slot.getDoctor().getEmail(),
                slot.getDoctor().getRole().name(),
                String.format("Updated one-time slot ID %d: %s %s-%s", slotId, date, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public void deleteOneTimeSlot(Long slotId) {
        DoctorOneTimeSlot slot = oneTimeRepo.findById(slotId).orElseThrow();
        if (ScheduleValidationUtils.isOneTimeSlotPast(slot.getDate(), slot.getEndTime())) {
            throw new IllegalArgumentException("Cannot delete a one-time slot in the past.");
        }
        oneTimeRepo.deleteById(slotId);

        // Audit log
        auditLogService.logAction(
                "One-Time Slot Deleted",
                slot.getDoctor().getEmail(),
                slot.getDoctor().getRole().name(),
                String.format("Deleted one-time slot ID %d", slotId),
                null,
                slot.toString()
        );
    }

    // --- Recurring Breaks CRUD ---

    public List<DoctorRecurringBreak> getRecurringBreaks(Long doctorId) {
        return breakRepo.findByDoctorIdAndActiveTrue(doctorId);
    }

    @Transactional
    public DoctorRecurringBreak addRecurringBreak(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot add a recurring break in the past.");
        }
        // Overlap check:
        if (!isAvailable(doctorId, day, start, end, null, "BREAK")) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringBreak brk = new DoctorRecurringBreak();
        brk.setDoctor(doctor);
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);

        DoctorRecurringBreak saved = breakRepo.save(brk);

        // Audit log
        auditLogService.logAction(
                "Recurring Break Added",
                doctor.getEmail(),
                doctor.getRole().name(),
                String.format("Added recurring break: %s %s-%s", day, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public DoctorRecurringBreak updateRecurringBreak(Long doctorId, Long breakId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot update a recurring break in the past.");
        }
        // Overlap check (exclude self)
        if (!isAvailable(doctorId, day, start, end, breakId, "BREAK")) {
            throw new IllegalArgumentException("A slot or break already exists in this time range.");
        }
        DoctorRecurringBreak brk = breakRepo.findById(breakId).orElseThrow();
        if (!Objects.equals(brk.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);

        DoctorRecurringBreak saved = breakRepo.save(brk);

        // Audit log
        auditLogService.logAction(
                "Recurring Break Updated",
                brk.getDoctor().getEmail(),
                brk.getDoctor().getRole().name(),
                String.format("Updated recurring break ID %d: %s %s-%s", breakId, day, start, end),
                null,
                saved.toString()
        );
        return saved;
    }

    @Transactional
    public void deleteRecurringBreak(Long breakId) {
        DoctorRecurringBreak brk = breakRepo.findById(breakId).orElseThrow();
        if (ScheduleValidationUtils.isRecurringPast(brk.getDayOfWeek(), brk.getEndTime())) {
            throw new IllegalArgumentException("Cannot delete a recurring break in the past.");
        }
        breakRepo.deleteById(brk.getId());

        // Audit log
        auditLogService.logAction(
                "Recurring Break Deleted",
                brk.getDoctor().getEmail(),
                brk.getDoctor().getRole().name(),
                String.format("Deleted recurring break ID %d", breakId),
                null,
                brk.toString()
        );
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
        List<DoctorOneTimeSlot> oneTimeSlots = oneTimeRepo.findByDoctorIdAndDateBetween(doctorId, start, end);
        Set<DayOfWeek> rangeDays = start.datesUntil(end.plusDays(1))
                .map(LocalDate::getDayOfWeek).collect(Collectors.toSet());
        List<DoctorRecurringSchedule> recurringSlots = recurringRepo.findByDoctorIdAndActiveTrue(doctorId).stream()
                .filter(slot -> rangeDays.contains(slot.getDayOfWeek()))
                .collect(Collectors.toList());
        List<DoctorRecurringBreak> recurringBreaks = breakRepo.findByDoctorIdAndActiveTrue(doctorId).stream()
                .filter(brk -> rangeDays.contains(brk.getDayOfWeek()))
                .collect(Collectors.toList());
        return new DoctorFullScheduleDto(recurringSlots, oneTimeSlots, recurringBreaks);
    }

    // --- Batch Replace / Template Import ---

    @Transactional
    public void replaceWeek(Long doctorId, List<RecurringSlotDto> weekSchedule) {
        recurringRepo.deleteByDoctorId(doctorId);
        for (RecurringSlotDto dto : weekSchedule) {
            addRecurringSlot(doctorId, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        }
        auditLogService.logAction(
                "Week Schedule Replaced",
                doctorRepository.findById(doctorId).map(Doctor::getEmail).orElse("UNKNOWN"),
                "DOCTOR",
                "Replaced all recurring slots for the week.",
                null,
                weekSchedule.toString()
        );
    }

    @Transactional
    public DoctorFullScheduleDto importTemplate(Long doctorId, DoctorFullScheduleDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        recurringRepo.deleteByDoctorId(doctorId);
        oneTimeRepo.deleteByDoctorId(doctorId);
        breakRepo.deleteByDoctorId(doctorId);

        if (dto.getRecurringSlots() != null) {
            dto.getRecurringSlots().forEach(slot -> {
                slot.setDoctor(doctor);
                slot.setId(null); // ensure new
                recurringRepo.save(slot);
            });
        }
        if (dto.getOneTimeSlots() != null) {
            dto.getOneTimeSlots().forEach(slot -> {
                slot.setDoctor(doctor);
                slot.setId(null); // ensure new
                oneTimeRepo.save(slot);
            });
        }
        if (dto.getRecurringBreaks() != null) {
            dto.getRecurringBreaks().forEach(brk -> {
                brk.setDoctor(doctor);
                brk.setId(null); // ensure new
                breakRepo.save(brk);
            });
        }
        auditLogService.logAction(
                "Schedule Template Imported",
                doctor.getEmail(),
                doctor.getRole().name(),
                "Imported a schedule template.",
                null,
                dto.toString()
        );
        return getFullSchedule(doctorId);
    }

    // --- Slot Removal Request CRUD (with audit logging) ---

    @Transactional
    public SlotRemovalRequestDto createSlotRemovalRequest(Long doctorId, SlotRemovalRequestCreateDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        // ==== BLOCK DUPLICATES ====
        boolean exists = slotRemovalRequestRepository.existsByDoctor_IdAndSlotTypeAndSlotIdAndStatus(
                doctorId,
                dto.getSlotType(),
                dto.getSlotId(),
                "PENDING"
        );
        if (exists) {
            throw new IllegalStateException("A pending removal request already exists for this slot.");
        }
        // ==========================

        SlotRemovalRequest req = new SlotRemovalRequest();
        req.setSlotType(dto.getSlotType());
        req.setSlotId(dto.getSlotId());
        req.setDoctor(doctor);
        req.setReason(dto.getReason());
        req.setStatus("PENDING");
        req.setRequestedAt(LocalDateTime.now());
        // Immediately mark the targeted slot/break as inactive
        switch (dto.getSlotType()) {
            case "RECURRING" -> {
                DoctorRecurringSchedule slot = recurringRepo.findById(dto.getSlotId()).orElseThrow();
                if (Objects.equals(slot.getDoctor().getId(), doctorId)) {
                    slot.setActive(false);
                    recurringRepo.save(slot);
                }
            }
            case "BREAK" -> {
                DoctorRecurringBreak brk = breakRepo.findById(dto.getSlotId()).orElseThrow();
                if (Objects.equals(brk.getDoctor().getId(), doctorId)) {
                    brk.setActive(false);
                    breakRepo.save(brk);
                }
            }
        }

        try {
            slotRemovalRequestRepository.save(req);
            auditLogService.logAction(
                    "Slot Removal Requested",
                    doctor.getEmail(),
                    doctor.getRole().name(),
                    String.format("Requested removal for %s slot (ID: %d)", dto.getSlotType(), dto.getSlotId()),
                    null,
                    req.toString()
            );
        } catch (Exception e) {
            auditLogService.logAction(
                    "Slot Removal Request Failed",
                    doctor.getEmail(),
                    doctor.getRole().name(),
                    "Doctor attempted to request slot removal.",
                    e.getMessage(),
                    dto.toString()
            );
            throw e;
        }
        return toDto(req);
    }

    public List<SlotRemovalRequestDto> getRemovalRequestsForDoctor(Long doctorId) {
        return slotRemovalRequestRepository.findByDoctor_Id(doctorId)
                .stream().map(this::toDto).toList();
    }

    private SlotRemovalRequestDto toDto(SlotRemovalRequest req) {
        SlotRemovalRequestDto dto = new SlotRemovalRequestDto();
        dto.setId(req.getId());
        dto.setSlotType(req.getSlotType());
        dto.setSlotId(req.getSlotId());

        if (req.getDoctor() != null) {
            Doctor doctor = req.getDoctor();
            DoctorProfileDto doctorProfileDto = new DoctorProfileDto();
            doctorProfileDto.setId(doctor.getId());
            doctorProfileDto.setFirstName(doctor.getFirstName());
            doctorProfileDto.setLastName(doctor.getLastName());
            doctorProfileDto.setEmail(doctor.getEmail());
            doctorProfileDto.setPhoneNumber(doctor.getPhoneNumber());
            doctorProfileDto.setRole(String.valueOf(doctor.getRole()));
            doctorProfileDto.setSpecialty(doctor.getSpecialty());
            doctorProfileDto.setLicenseNumber(doctor.getLicenseNumber());
            doctorProfileDto.setAccountStatus(String.valueOf(doctor.getAccountStatus()));
            dto.setDoctor(doctorProfileDto);
        }
        dto.setReason(req.getReason());
        dto.setStatus(req.getStatus());
        dto.setRequestedAt(req.getRequestedAt());
        dto.setReviewedAt(req.getReviewedAt());
        dto.setReviewedByAdminId(req.getReviewedByAdminId());
        dto.setAdminNote(req.getAdminNote());

        return dto;
    }
}
