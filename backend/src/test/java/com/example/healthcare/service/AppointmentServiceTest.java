//package com.example.healthcare.service;
//
//import com.example.healthcare.entity.Appointment;
//import com.example.healthcare.entity.Doctor;
//import com.example.healthcare.entity.Patient;
//import com.example.healthcare.entity.enums.AppointmentStatus;
//import com.example.healthcare.exception.AppointmentConflictException;
//import com.example.healthcare.exception.AppointmentNotFoundException;
//import com.example.healthcare.repository.AppointmentRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AppointmentServiceTest {
//
//    @Mock
//    private AppointmentRepository appointmentRepository;
//
//    @InjectMocks
//    private AppointmentService appointmentService;
//
//    @Test
//    void testBookAppointment_Success() {
//        // Arrange
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//
//        Patient patient = new Patient();
//        patient.setId(2L);
//
//        Appointment appointment = new Appointment();
//        appointment.setDoctor(doctor);
//        appointment.setPatient(patient);
//        appointment.setAppointmentTime(LocalDateTime.now());
//
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(Collections.emptyList());
//        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
//
//        // Act
//        Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
//
//        // Assert
//        assertNotNull(bookedAppointment);
//        assertEquals(AppointmentStatus.BOOKED, bookedAppointment.getStatus());
//        verify(appointmentRepository, times(1)).save(appointment);
//    }
//
//    @Test
//    void testBookAppointment_NullAppointment() {
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () ->
//                appointmentService.bookAppointment(null)
//        );
//    }
//
//    @Test
//    void testBookAppointment_NullDoctor() {
//        // Arrange
//        Appointment appointment = new Appointment();
//        appointment.setDoctor(null);
//        appointment.setPatient(new Patient());
//        appointment.setAppointmentTime(LocalDateTime.now());
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () ->
//                appointmentService.bookAppointment(appointment)
//        );
//    }
//
//    @Test
//    void testBookAppointment_Conflict() {
//        // Arrange
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//
//        Patient patient = new Patient();
//        patient.setId(2L);
//
//        Appointment appointment = new Appointment();
//        appointment.setDoctor(doctor);
//        appointment.setPatient(patient);
//        appointment.setAppointmentTime(LocalDateTime.now());
//
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(List.of(new Appointment()));
//
//        // Act & Assert
//        assertThrows(AppointmentConflictException.class, () ->
//                appointmentService.bookAppointment(appointment)
//        );
//    }
//
//    @Test
//    void testIsAppointmentAvailable_NoConflict() {
//        // Arrange
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(Collections.emptyList());
//
//        // Act
//        boolean isAvailable = appointmentService.isAppointmentAvailable(1L, LocalDateTime.now());
//
//        // Assert
//        assertTrue(isAvailable);
//    }
//
//    @Test
//    void testIsAppointmentAvailable_Conflict() {
//        // Arrange
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(List.of(new Appointment()));
//
//        // Act
//        boolean isAvailable = appointmentService.isAppointmentAvailable(1L, LocalDateTime.now());
//
//        // Assert
//        assertFalse(isAvailable);
//    }
//
//    @Test
//    void testGetAllAppointments() {
//        // Arrange
//        when(appointmentRepository.findAllByIsDeletedFalse()).thenReturn(List.of(new Appointment()));
//
//        // Act
//        List<Appointment> appointments = appointmentService.getAllAppointments();
//
//        // Assert
//        assertEquals(1, appointments.size());
//    }
//
//    @Test
//    void testGetAppointment_Success() {
//        // Arrange
//        Appointment appointment = new Appointment();
//        when(appointmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(appointment));
//
//        // Act
//        Appointment foundAppointment = appointmentService.getAppointment(1L);
//
//        // Assert
//        assertNotNull(foundAppointment);
//    }
//
//    @Test
//    void testGetAppointment_NotFound() {
//        // Arrange
//        when(appointmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(AppointmentNotFoundException.class, () ->
//                appointmentService.getAppointment(1L)
//        );
//    }
//
//    @Test
//    void testUpdateAppointment_Success() {
//        // Arrange
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//
//        Appointment existing = new Appointment();
//        existing.setId(1L);
//        existing.setDoctor(doctor);
//        existing.setAppointmentTime(LocalDateTime.now());
//        existing.setStatus(AppointmentStatus.BOOKED);
//
//        Appointment updated = new Appointment();
//        updated.setAppointmentTime(LocalDateTime.now().plusHours(1));
//        updated.setStatus(AppointmentStatus.CANCELLED);
//
//        when(appointmentRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(Collections.emptyList());
//        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existing);
//
//        // Act
//        appointmentService.updateAppointment(1L, updated);
//
//        // Assert
//        assertEquals(updated.getAppointmentTime(), existing.getAppointmentTime());
//        assertEquals(updated.getStatus(), existing.getStatus());
//    }
//
//    @Test
//    void testUpdateAppointment_Conflict() {
//        // Arrange
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//
//        Appointment existing = new Appointment();
//        existing.setId(1L);
//        existing.setDoctor(doctor);
//        existing.setAppointmentTime(LocalDateTime.now());
//
//        Appointment updated = new Appointment();
//        updated.setAppointmentTime(LocalDateTime.now().plusMinutes(15));
//
//        when(appointmentRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
//        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(anyLong(), any(), any()))
//                .thenReturn(List.of(new Appointment()));
//
//        // Act & Assert
//        assertThrows(AppointmentConflictException.class, () ->
//                appointmentService.updateAppointment(1L, updated)
//        );
//    }
//
//    @Test
//    void testCancelAppointment_Success() {
//        // Arrange
//        Appointment appointment = new Appointment();
//        appointment.setId(1L);
//        appointment.setStatus(AppointmentStatus.BOOKED);
//
//        when(appointmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(appointment));
//
//        // Act
//        appointmentService.cancelAppointment(1L);
//
//        // Assert
//        assertTrue(appointment.isDeleted());
//        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
//    }
//
//    @Test
//    void testCancelAppointment_NotFound() {
//        // Arrange
//        when(appointmentRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(AppointmentNotFoundException.class, () ->
//                appointmentService.cancelAppointment(1L)
//        );
//    }
//
//    @Test
//    void testCreateAppointment_DefaultIsDeletedFalse() {
//        Appointment appointment = new Appointment();
//        appointment.setDoctor(new Doctor());
//        appointment.setPatient(new Patient());
//        appointment.setAppointmentTime(LocalDateTime.now());
//        appointment.setStatus(AppointmentStatus.BOOKED);
//
//        Appointment saved = appointmentRepository.save(appointment);
//
//        assertFalse(saved.isDeleted()); // ✅ Ensure default value is set
//    }
//
//}