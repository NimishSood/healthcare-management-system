package com.example.healthcare.service;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void testGetPatientById() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPatientByIdNotFound() {
        when(patientRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(1L));
    }
}