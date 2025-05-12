//package com.example.healthcare.service;
//
//import com.example.healthcare.entity.Patient;
//import com.example.healthcare.exception.PatientNotFoundException;
//import com.example.healthcare.repository.PatientRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Optional;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PatientServiceTest {
//
//    @Mock
//    private PatientRepository patientRepository;
//
//    @InjectMocks
//    private PatientService patientService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // getAllPatients
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
//        when(patientRepository.findAllByIsDeletedFalse()).thenReturn(patients);
//
//        // Act
//        List<Patient> result = patientService.getAllPatients();
//
//        // Assert
//        assertEquals(2, result.size());
//        verify(patientRepository, times(1)).findAllByIsDeletedFalse();
//    }
//
//    @Test
//    void testGetAllPatients_WhenNoPatientsExist() {
//        // Arrange
//        when(patientRepository.findAllByIsDeletedFalse()).thenReturn(Collections.emptyList());
//
//        // Act
//        List<Patient> result = patientService.getAllPatients();
//
//        // Assert
//        assertTrue(result.isEmpty());
//        verify(patientRepository, times(1)).findAllByIsDeletedFalse();
//    }
//
//    // getPatientById
//    @Test
//    void testGetPatientById_Success() {
//        // Arrange
//        Patient patient = new Patient();
//        patient.setId(1L);
//        patient.setFirstName("Alice");
//
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(patient));
//
//        // Act
//        Patient result = patientService.getPatientById(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        verify(patientRepository, times(1)).findByIdAndIsDeletedFalse(1L);
//    }
//
//    @Test
//    void testGetPatientById_NotFound() {
//        // Arrange
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
//                () -> patientService.getPatientById(1L));
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//    // createPatient
//    @Test
//    void testCreatePatient() {
//        // Arrange
//        Patient patient = new Patient();
//        patient.setFirstName("Bob");
//
//        // We assume save returns the same patient
//        when(patientRepository.save(patient)).thenReturn(patient);
//
//        // Act
//        patientService.createPatient(patient);
//
//        // Assert
//        verify(patientRepository, times(1)).save(patient);
//    }
//
//    // updatePatient
//    @Test
//    void testUpdatePatient_Success() {
//        // Arrange
//        Patient existing = new Patient();
//        existing.setId(1L);
//        existing.setFirstName("OldFirstName");
//        existing.setLastName("OldLastName");
//        existing.setPhoneNumber("123");
//        existing.setInsuranceProvider("OldInsurance");
//
//        Patient updated = new Patient();
//        updated.setFirstName("NewFirstName");
//        updated.setLastName("NewLastName");
//        updated.setPhoneNumber("456");
//        updated.setInsuranceProvider("NewInsurance");
//
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
//        when(patientRepository.save(existing)).thenReturn(existing);
//
//        // Act
//        patientService.updatePatient(1L, updated);
//
//        // Assert: verify that the fields have been updated
//        assertEquals("NewFirstName", existing.getFirstName());
//        assertEquals("NewLastName", existing.getLastName());
//        assertEquals("456", existing.getPhoneNumber());
//        assertEquals("NewInsurance", existing.getInsuranceProvider());
//        verify(patientRepository, times(1)).save(existing);
//    }
//
//    @Test
//    void testUpdatePatient_NotFound() {
//        // Arrange
//        Patient updated = new Patient();
//        updated.setFirstName("NewFirstName");
//
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
//                () -> patientService.updatePatient(1L, updated));
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//    // deletePatient
//    @Test
//    void testDeletePatient_Success() {
//        // Arrange
//        Patient patient = new Patient();
//        patient.setId(1L);
//        patient.setDeleted(false);
//
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(patient));
//        when(patientRepository.save(patient)).thenReturn(patient);
//
//        // Act
//        patientService.deletePatient(1L);
//
//        // Assert
//        assertTrue(patient.isDeleted());
//        verify(patientRepository, times(1)).save(patient);
//    }
//
//    @Test
//    void testDeletePatient_NotFound() {
//        // Arrange
//        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
//                () -> patientService.deletePatient(1L));
//        assertEquals("Patient not found with id: 1", exception.getMessage());
//    }
//
//}
