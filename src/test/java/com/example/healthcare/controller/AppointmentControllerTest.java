package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.exception.DoctorNotFoundException;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import com.example.healthcare.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookAppointment_Success() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(2L);

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setPatientId(2L);
        request.setAppointmentTime(LocalDateTime.now());

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(appointmentService.bookAppointment(any(Appointment.class))).thenReturn(new Appointment());

        // Act
        ResponseEntity<?> response = appointmentController.bookAppointment(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(appointmentService, times(1)).bookAppointment(any(Appointment.class));
    }

    @Test
    void testBookAppointment_DoctorNotFound() {
        // Arrange
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setPatientId(2L);
        request.setAppointmentTime(LocalDateTime.now());

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> {
            appointmentController.bookAppointment(request);
        });

        assertEquals("Doctor not found with id: 1", exception.getMessage());
        verify(appointmentService, never()).bookAppointment(any(Appointment.class));
    }

    @Test
    void testBookAppointment_PatientNotFound() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setPatientId(2L);
        request.setAppointmentTime(LocalDateTime.now());

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> {
            appointmentController.bookAppointment(request);
        });

        assertEquals("Patient not found with id: 2", exception.getMessage());
        verify(appointmentService, never()).bookAppointment(any(Appointment.class));
    }

    @Test
    void testBookAppointment_Conflict() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(2L);

        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setPatientId(2L);
        request.setAppointmentTime(LocalDateTime.now());

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(appointmentService.bookAppointment(any(Appointment.class)))
                .thenThrow(new AppointmentConflictException("Conflict"));

        // Act & Assert
        AppointmentConflictException exception = assertThrows(AppointmentConflictException.class, () -> {
            appointmentController.bookAppointment(request);
        });

        assertEquals("Conflict", exception.getMessage());
        verify(appointmentService, times(1)).bookAppointment(any(Appointment.class));
    }
}