//package com.example.healthcare.controller;
//
//import com.example.healthcare.entity.Patient;
//import com.example.healthcare.exception.PatientNotFoundException;
//import com.example.healthcare.service.PatientService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PatientControllerTest {
//
//    @Mock
//    private PatientService patientService;
//
//    @InjectMocks
//    private PatientController patientController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // GET /patients
//    @Test
//    void testGetAllPatients_WhenPatientsExist() {
//        // Arrange
//        Patient p1 = new Patient();
//        p1.setId(1L);
//        p1.setFirstName("John");
//        p1.setLastName("Doe");
//
//        Patient p2 = new Patient();
//        p2.setId(2L);
//        p2.setFirstName("Jane");
//        p2.setLastName("Smith");
//
//        List<Patient> patients = Arrays.asList(p1, p2);
//        when(patientService.getAllPatients()).thenReturn(patients);
//
//        // Act
//        ResponseEntity<List<Patient>> response = patientController.getAllPatients();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//        verify(patientService, times(1)).getAllPatients();
//    }
//
//    @Test
//    void testGetAllPatients_WhenNoPatientsExist() {
//        // Arrange
//        when(patientService.getAllPatients()).thenReturn(Collections.emptyList());
//
//        // Act
//        ResponseEntity<List<Patient>> response = patientController.getAllPatients();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isEmpty());
//        verify(patientService, times(1)).getAllPatients();
//    }
//
//    // GET /patients/{id}
//    @Test
//    void testGetPatient_Success() {
//        // Arrange
//        Patient patient = new Patient();
//        patient.setId(1L);
//        patient.setFirstName("Alice");
//        patient.setLastName("Wonderland");
//
//        when(patientService.getPatientById(1L)).thenReturn(patient);
//
//        // Act
//        ResponseEntity<Patient> response = patientController.getPatient(1L);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1L, response.getBody().getId());
//    }
//
//    @Test
//    void testGetPatient_NotFound() {
//        // Arrange
//        when(patientService.getPatientById(1L))
//                .thenThrow(new PatientNotFoundException("Patient not found with id: 1"));
//
//        // If testing the controller directly (without a global exception handler or try-catch block),
//        // the exception will propagate. In that case, you can either assert that the exception is thrown:
//        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
//            patientController.getPatient(1L);
//        });
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//    // POST /patients
//    @Test
//    void testCreatePatient_Success() {
//        // Arrange
//        Patient patient = new Patient();
//        patient.setFirstName("Bob");
//        patient.setLastName("Marley");
//
//        // We assume createPatient does not return anything; it just saves the patient.
//        // So, simply verify that patientService.createPatient() is called.
//        doNothing().when(patientService).createPatient(patient);
//
//        // Act
//        ResponseEntity<String> response = patientController.createPatient(patient);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Patient created successfully", response.getBody());
//        verify(patientService, times(1)).createPatient(patient);
//    }
//
//    // PUT /patients/{id}
//    @Test
//    void testUpdatePatient_Success() {
//        // Arrange
//        Patient updatedPatient = new Patient();
//        updatedPatient.setFirstName("Charlie");
//        updatedPatient.setLastName("Brown");
//
//        doNothing().when(patientService).updatePatient(1L, updatedPatient);
//
//        // Act
//        ResponseEntity<String> response = patientController.updatePatient(1L, updatedPatient);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Patient updated successfully", response.getBody());
//        verify(patientService, times(1)).updatePatient(1L, updatedPatient);
//    }
//
//    @Test
//    void testUpdatePatient_NotFound() {
//        // Arrange
//        Patient updatedPatient = new Patient();
//        updatedPatient.setFirstName("Charlie");
//        updatedPatient.setLastName("Brown");
//
//        doThrow(new PatientNotFoundException("Patient not found with id: 1"))
//                .when(patientService).updatePatient(1L, updatedPatient);
//
//        // Act & Assert
//        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
//            patientController.updatePatient(1L, updatedPatient);
//        });
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//    // DELETE /patients/{id}
//    @Test
//    void testDeletePatient_Success() {
//        // Arrange
//        doNothing().when(patientService).deletePatient(1L);
//
//        // Act
//        ResponseEntity<String> response = patientController.deletePatient(1L);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Patient deleted successfully", response.getBody());
//        verify(patientService, times(1)).deletePatient(1L);
//    }
//
//    @Test
//    void testDeletePatient_NotFound() {
//        // Arrange
//        doThrow(new PatientNotFoundException("Patient not found with id: 1"))
//                .when(patientService).deletePatient(1L);
//
//        // Act & Assert
//        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
//            patientController.deletePatient(1L);
//        });
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//}
