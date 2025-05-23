package com.example.healthcare.service;

import com.example.healthcare.dto.DoctorSchedule.*;
import com.example.healthcare.dto.Profiles.DoctorProfileDto;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.DoctorSchedule.*;
import com.example.healthcare.repository.DoctorSchedule.*;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.AuditLogService; // Make sure you import this!
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
    private final AuditLogService auditLogService; // Injected for logging

    // --- Recurring Schedule CRUD ---

    public List<DoctorRecurringSchedule> getRecurringSchedule(Long doctorId) {
        return recurringRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorRecurringSchedule addRecurringSlot(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot add a recurring slot in the past.");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringSchedule slot = new DoctorRecurringSchedule();
        slot.setDoctor(doctor);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        // TODO: Check for overlaps with other recurring slots or breaks

        DoctorRecurringSchedule saved = recurringRepo.save(slot);

        // --- Audit log for slot creation ---
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
        DoctorRecurringSchedule slot = recurringRepo.findById(slotId).orElseThrow();
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);

        DoctorRecurringSchedule saved = recurringRepo.save(slot);

        // --- Audit log for slot update ---
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

        // --- Audit log for slot deletion ---
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
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorOneTimeSlot slot = new DoctorOneTimeSlot();
        slot.setDoctor(doctor);
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);

        DoctorOneTimeSlot saved = oneTimeRepo.save(slot);

        // --- Audit log for one-time slot creation ---
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
        DoctorOneTimeSlot slot = oneTimeRepo.findById(slotId).orElseThrow();
        if (!Objects.equals(slot.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(available);

        DoctorOneTimeSlot saved = oneTimeRepo.save(slot);

        // --- Audit log for one-time slot update ---
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

        // --- Audit log for one-time slot deletion ---
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
        return breakRepo.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorRecurringBreak addRecurringBreak(Long doctorId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (ScheduleValidationUtils.isRecurringPast(day, end)) {
            throw new IllegalArgumentException("Cannot add a recurring break in the past.");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        DoctorRecurringBreak brk = new DoctorRecurringBreak();
        brk.setDoctor(doctor);
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);

        DoctorRecurringBreak saved = breakRepo.save(brk);

        // --- Audit log for recurring break creation ---
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
        DoctorRecurringBreak brk = breakRepo.findById(breakId).orElseThrow();
        if (!Objects.equals(brk.getDoctor().getId(), doctorId)) throw new RuntimeException("Unauthorized");
        brk.setDayOfWeek(day);
        brk.setStartTime(start);
        brk.setEndTime(end);

        DoctorRecurringBreak saved = breakRepo.save(brk);

        // --- Audit log for recurring break update ---
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

        // --- Audit log for recurring break deletion ---
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

        // --- Audit log for batch replace ---
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

        // --- Audit log for template import ---
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

    /**
     * Doctor submits a slot removal request for a slot they want deleted.
     */
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

        try {
            slotRemovalRequestRepository.save(req);

            // --- Audit log for slot removal request ---
            auditLogService.logAction(
                    "Slot Removal Requested",
                    doctor.getEmail(),
                    doctor.getRole().name(),
                    String.format("Requested removal for %s slot (ID: %d)", dto.getSlotType(), dto.getSlotId()),
                    null,
                    req.toString()
            );
        } catch (Exception e) {
            // --- Audit log for failed removal request ---
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


    /**
     * Get all removal requests for a doctor.
     */
    public List<SlotRemovalRequestDto> getRemovalRequestsForDoctor(Long doctorId) {
        return slotRemovalRequestRepository.findByDoctor_Id(doctorId)
                .stream().map(this::toDto).toList();
    }

    // Helper method to convert entity to DTO
    private SlotRemovalRequestDto toDto(SlotRemovalRequest req) {
        SlotRemovalRequestDto dto = new SlotRemovalRequestDto();
        dto.setId(req.getId());
        dto.setSlotType(req.getSlotType());
        dto.setSlotId(req.getSlotId());

        // Map Doctor entity to DoctorProfileDto
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
            // ...set any other fields you want visible in the profile DTO

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


    public boolean isAvailable(Long id, LocalDate date, LocalTime start, LocalTime end)
    {
        // TODO: Implement overlap and conflict checking when appointments are built
        return true;
    }
}
