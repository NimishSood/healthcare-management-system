//package com.example.healthcare.service;
//
//import com.example.healthcare.entity.Doctor;
//import com.example.healthcare.exception.DoctorNotFoundException;
//import com.example.healthcare.repository.DoctorRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class DoctorServiceTest {
//
//    @Mock
//    private DoctorRepository doctorRepository;
//
//    @InjectMocks
//    private DoctorService doctorService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // getAllDoctors
//    @Test
//    void testGetAllDoctors_WhenDoctorsExist() {
//        Doctor d1 = new Doctor();
//        d1.setId(1L);
//        d1.setFirstName("Eva");
//
//        Doctor d2 = new Doctor();
//        d2.setId(2L);
//        d2.setFirstName("Frank");
//
//        List<Doctor> doctors = Arrays.asList(d1, d2);
//        when(doctorRepository.findAllByIsDeletedFalse()).thenReturn(doctors);
//
//        List<Doctor> result = doctorService.getAllDoctors();
//        assertEquals(2, result.size());
//        verify(doctorRepository, times(1)).findAllByIsDeletedFalse();
//    }
//
//    @Test
//    void testGetAllDoctors_WhenNoDoctorsExist() {
//        when(doctorRepository.findAllByIsDeletedFalse()).thenReturn(Collections.emptyList());
//
//        List<Doctor> result = doctorService.getAllDoctors();
//        assertTrue(result.isEmpty());
//        verify(doctorRepository, times(1)).findAllByIsDeletedFalse();
//    }
//
//    // getDoctorById
//    @Test
//    void testGetDoctorById_Success() {
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//        doctor.setFirstName("George");
//
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(doctor));
//
//        Doctor result = doctorService.getDoctorById(1L);
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        verify(doctorRepository, times(1)).findByIdAndIsDeletedFalse(1L);
//    }
//
//    @Test
//    void testGetDoctorById_NotFound() {
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorService.getDoctorById(1L));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//
//    // createDoctor
//    @Test
//    void testCreateDoctor() {
//        Doctor doctor = new Doctor();
//        doctor.setFirstName("Helen");
//
//        when(doctorRepository.save(doctor)).thenReturn(doctor);
//        doctorService.createDoctor(doctor);
//        verify(doctorRepository, times(1)).save(doctor);
//    }
//
//    // updateDoctor
//    @Test
//    void testUpdateDoctor_Success() {
//        Doctor existing = new Doctor();
//        existing.setId(1L);
//        existing.setFirstName("Ivy");
//        existing.setLastName("Ingram");
//
//        Doctor updated = new Doctor();
//        updated.setFirstName("Isabel");
//        updated.setLastName("Irwin");
//        updated.setSpecialty("Cardiology");
//        updated.setLicenseNumber("LIC123");
//        updated.setYearsOfExperience(10);
//
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
//        when(doctorRepository.save(existing)).thenReturn(existing);
//
//        doctorService.updateDoctor(1L, updated);
//        assertEquals("Isabel", existing.getFirstName());
//        assertEquals("Irwin", existing.getLastName());
//        assertEquals("Cardiology", existing.getSpecialty());
//        assertEquals("LIC123", existing.getLicenseNumber());
//        assertEquals(10, existing.getYearsOfExperience());
//        verify(doctorRepository, times(1)).save(existing);
//    }
//
//    @Test
//    void testUpdateDoctor_NotFound() {
//        Doctor updated = new Doctor();
//        updated.setFirstName("Isabel");
//
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorService.updateDoctor(1L, updated));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//
//    // deleteDoctor
//    @Test
//    void testDeleteDoctor_Success() {
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//        doctor.setDeleted(false);
//
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(doctor));
//        when(doctorRepository.save(doctor)).thenReturn(doctor);
//
//        doctorService.deleteDoctor(1L);
//        assertTrue(doctor.isDeleted());
//        verify(doctorRepository, times(1)).save(doctor);
//    }
//
//    @Test
//    void testDeleteDoctor_NotFound() {
//        when(doctorRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
//
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorService.deleteDoctor(1L));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//}
