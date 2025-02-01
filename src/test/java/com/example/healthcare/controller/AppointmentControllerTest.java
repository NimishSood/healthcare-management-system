package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentConflictException;
import com.example.healthcare.exception.AppointmentNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        // Act
        ResponseEntity<?> response = appointmentController.bookAppointment(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Doctor not found with id: 1", response.getBody());
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

        // Act
        ResponseEntity<?> response = appointmentController.bookAppointment(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Patient not found with id: 2", response.getBody());
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

        // Act
        ResponseEntity<?> response = appointmentController.bookAppointment(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody());
        verify(appointmentService, times(1)).bookAppointment(any(Appointment.class));
    }

    @Test
    void testGetAllAppointments() {
        // Arrange
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetAppointment_Success() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(appointmentService.getAppointment(1L)).thenReturn(appointment);

        // Act
        ResponseEntity<Appointment> response = (ResponseEntity<Appointment>) appointmentController.getAppointment(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetAppointment_NotFound() {
        // Arrange: Mock the service to throw the exception
        when(appointmentService.getAppointment(1L))
                .thenThrow(new AppointmentNotFoundException("Appointment not found"));

        // Act: Directly call the controller method
        ResponseEntity<?> response = appointmentController.getAppointment(1L);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Appointment not found", response.getBody());
    }




    @Test
    void testUpdateAppointment_Success() {
        // Arrange
        Appointment updated = new Appointment();
        updated.setAppointmentTime(LocalDateTime.now());
        updated.setStatus(AppointmentStatus.CANCELLED);

        // Act
        ResponseEntity<String> response = appointmentController.updateAppointment(1L, updated);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment updated successfully", response.getBody());
    }

    @Test
    void testUpdateAppointment_NotFound() {
        // Arrange
        Appointment updated = new Appointment();
        updated.setAppointmentTime(LocalDateTime.now());
        updated.setStatus(AppointmentStatus.CANCELLED);

        doThrow(new AppointmentNotFoundException("Appointment not found"))
                .when(appointmentService).updateAppointment(1L, updated);

        // Act
        ResponseEntity<String> response = appointmentController.updateAppointment(1L, updated);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Appointment not found", response.getBody());
    }

    @Test
    void testCancelAppointment_Success() {
        // Act
        ResponseEntity<String> response = appointmentController.cancelAppointment(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment canceled", response.getBody());
    }

    @Test
    void testCancelAppointment_NotFound() {
        // Arrange
        doThrow(new AppointmentNotFoundException("Appointment not found"))
                .when(appointmentService).cancelAppointment(1L);

        // Act
        ResponseEntity<String> response = appointmentController.cancelAppointment(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Appointment not found", response.getBody());
    }
}