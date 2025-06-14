package com.example.healthcare.service;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.Appointment;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.repository.UserRepository;
import com.example.healthcare.entity.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppointmentBookingScheduleTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private DoctorScheduleService doctorScheduleService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor();
        doctor.setId(1L);
        patient = new Patient();
        patient.setId(2L);
    }

    @Test
    void bookAppointment_DoctorUnavailable_throws() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentTime(1L, time)).thenReturn(false);
        when(doctorScheduleService.isAppointmentTimeAvailable(1L, time)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.bookAppointment(2L, 1L, time));
        assertTrue(ex.getMessage().contains("not available"));
    }

    @Test
    void bookAppointment_DoctorAvailable_saves() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentTime(1L, time)).thenReturn(false);
        when(doctorScheduleService.isAppointmentTimeAvailable(1L, time)).thenReturn(true);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        appointmentService.bookAppointment(2L, 1L, time);

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}