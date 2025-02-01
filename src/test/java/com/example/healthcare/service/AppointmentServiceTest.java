package com.example.healthcare.service;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void testBookAppointment() {
        // Create a Doctor and set its ID
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        // Create Appointment and set the Doctor
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor); // Correctly set the Doctor object
        appointment.setAppointmentTime(LocalDateTime.now());

        // Mock repository behavior
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Mock the save method to return the appointment
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Call the service method
        Appointment bookedAppointment = appointmentService.bookAppointment(appointment);

        // Assertions
        assertNotNull(bookedAppointment);
        assertEquals(AppointmentStatus.BOOKED, bookedAppointment.getStatus());
    }

    @Test
    void testBookAppointmentConflict() {
        // Create a Doctor and set its ID
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        // Create Appointment and set the Doctor
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor); // Correctly set the Doctor object
        appointment.setAppointmentTime(LocalDateTime.now());

        // Mock repository to return a conflicting appointment
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(new Appointment()));

        // Verify exception is thrown
        assertThrows(AppointmentConflictException.class, () ->
                appointmentService.bookAppointment(appointment)
        );
    }
}