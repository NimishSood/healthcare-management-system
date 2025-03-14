package com.example.healthcare.service;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AdminService adminService;

    private User adminUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // Create a sample user representing an admin.
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        // (Optionally, set role if needed.)
    }

    @Test
    public void testAddDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Doc");
        doctor.setLastName("Tor");

        adminService.addDoctor(adminUser, doctor);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void testAddPatient() {
        Patient patient = new Patient();
        patient.setFirstName("Pat");
        patient.setLastName("Ient");

        adminService.addPatient(adminUser, patient);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    public void testRemoveDoctor() {
        Long doctorId = 10L;
        adminService.removeDoctor(adminUser, doctorId);
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    public void testRemovePatient() {
        Long patientId = 20L;
        adminService.removePatient(adminUser, patientId);
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    public void testGetAllDoctors() {
        Doctor doctor1 = new Doctor();
        Doctor doctor2 = new Doctor();
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = adminService.getAllDoctors(adminUser);
        verify(doctorRepository, times(1)).findAll();
        assertEquals(doctors, result);
    }

    @Test
    public void testGetAllPatients() {
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = adminService.getAllPatients(adminUser);
        verify(patientRepository, times(1)).findAll();
        assertEquals(patients, result);
    }
}
