package com.example.healthcare.service;

import com.example.healthcare.entity.DoctorSchedule.DoctorOneTimeSlot;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringBreak;
import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringSchedule;
import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.DoctorSchedule.DoctorOneTimeSlotRepository;
import com.example.healthcare.repository.DoctorSchedule.DoctorRecurringBreakRepository;
import com.example.healthcare.repository.DoctorSchedule.DoctorRecurringScheduleRepository;
import com.example.healthcare.repository.DoctorSchedule.SlotRemovalRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorScheduleServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorRecurringScheduleRepository recurringRepo;
    @Mock
    private DoctorOneTimeSlotRepository oneTimeRepo;
    @Mock
    private DoctorRecurringBreakRepository breakRepo;
    @Mock
    private SlotRemovalRequestRepository slotRemovalRequestRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private DoctorScheduleService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableSlots_basicScenario() {
        Long docId = 1L;
        LocalDate date = LocalDate.of(2024, 4, 15); // Monday

        DoctorRecurringSchedule rec = new DoctorRecurringSchedule();
        rec.setStartTime(LocalTime.of(9,0));
        rec.setEndTime(LocalTime.of(12,0));

        DoctorRecurringBreak brk = new DoctorRecurringBreak();
        brk.setStartTime(LocalTime.of(10,0));
        brk.setEndTime(LocalTime.of(10,30));

        DoctorOneTimeSlot extra = new DoctorOneTimeSlot();
        extra.setDate(date);
        extra.setStartTime(LocalTime.of(14,0));
        extra.setEndTime(LocalTime.of(15,0));
        extra.setAvailable(true);

        Appointment appt = new Appointment();
        appt.setAppointmentTime(LocalDateTime.of(date, LocalTime.of(9,30)));
        appt.setStatus(AppointmentStatus.BOOKED);

        when(recurringRepo.findByDoctorIdAndDayOfWeekAndActiveTrue(docId, DayOfWeek.MONDAY))
                .thenReturn(List.of(rec));
        when(breakRepo.findByDoctorIdAndDayOfWeekAndActiveTrue(docId, DayOfWeek.MONDAY))
                .thenReturn(List.of(brk));
        when(oneTimeRepo.findByDoctorIdAndDate(docId, date))
                .thenReturn(List.of(extra));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(eq(docId), any(), any()))
                .thenReturn(List.of(appt));

        List<LocalTime> slots = service.getAvailableSlots(docId, date, Duration.ofMinutes(30));

        List<LocalTime> expected = List.of(
                LocalTime.of(9,0),
                LocalTime.of(10,30),
                LocalTime.of(11,0),
                LocalTime.of(11,30),
                LocalTime.of(14,0),
                LocalTime.of(14,30)
        );
        assertEquals(expected, slots);
    }
}