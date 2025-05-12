//package com.example.healthcare.controller;
//
//import com.example.healthcare.entity.Doctor;
//import com.example.healthcare.exception.DoctorNotFoundException;
//import com.example.healthcare.service.DoctorService;
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
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class DoctorControllerTest {
//
//    @Mock
//    private DoctorService doctorService;
//
//    @InjectMocks
//    private DoctorController doctorController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // GET all doctors
//    @Test
//    void testGetAllDoctors_WhenDoctorsExist() {
//        Doctor d1 = new Doctor();
//        d1.setId(1L);
//        d1.setFirstName("Emily");
//        d1.setLastName("Evans");
//
//        Doctor d2 = new Doctor();
//        d2.setId(2L);
//        d2.setFirstName("Frank");
//        d2.setLastName("Foster");
//
//        List<Doctor> doctors = Arrays.asList(d1, d2);
//        when(doctorService.getAllDoctors()).thenReturn(doctors);
//
//        ResponseEntity<List<Doctor>> response = doctorController.getAllDoctors();
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//        verify(doctorService, times(1)).getAllDoctors();
//    }
//
//    @Test
//    void testGetAllDoctors_WhenNoDoctorsExist() {
//        when(doctorService.getAllDoctors()).thenReturn(Collections.emptyList());
//
//        ResponseEntity<List<Doctor>> response = doctorController.getAllDoctors();
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isEmpty());
//        verify(doctorService, times(1)).getAllDoctors();
//    }
//
//    // GET a single doctor
//    @Test
//    void testGetDoctor_Success() {
//        Doctor doctor = new Doctor();
//        doctor.setId(1L);
//        doctor.setFirstName("Grace");
//        doctor.setLastName("Green");
//
//        when(doctorService.getDoctorById(1L)).thenReturn(doctor);
//
//        ResponseEntity<Doctor> response = doctorController.getDoctor(1L);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1L, response.getBody().getId());
//    }
//
//    @Test
//    void testGetDoctor_NotFound() {
//        when(doctorService.getDoctorById(1L))
//                .thenThrow(new DoctorNotFoundException("Doctor not found with id: 1"));
//
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorController.getDoctor(1L));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//
//    // POST create doctor
//    @Test
//    void testCreateDoctor_Success() {
//        Doctor doctor = new Doctor();
//        doctor.setFirstName("Hannah");
//        doctor.setLastName("Hill");
//
//        doNothing().when(doctorService).createDoctor(doctor);
//
//        ResponseEntity<String> response = doctorController.createDoctor(doctor);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Doctor created successfully", response.getBody());
//        verify(doctorService, times(1)).createDoctor(doctor);
//    }
//
//    // PUT update doctor
//    @Test
//    void testUpdateDoctor_Success() {
//        Doctor updatedDoctor = new Doctor();
//        updatedDoctor.setFirstName("Ivan");
//        updatedDoctor.setLastName("Ingram");
//
//        doNothing().when(doctorService).updateDoctor(1L, updatedDoctor);
//
//        ResponseEntity<String> response = doctorController.updateDoctor(1L, updatedDoctor);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Doctor updated successfully", response.getBody());
//        verify(doctorService, times(1)).updateDoctor(1L, updatedDoctor);
//    }
//
//    @Test
//    void testUpdateDoctor_NotFound() {
//        Doctor updatedDoctor = new Doctor();
//        updatedDoctor.setFirstName("Ivan");
//
//        doThrow(new DoctorNotFoundException("Doctor not found with id: 1"))
//                .when(doctorService).updateDoctor(1L, updatedDoctor);
//
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorController.updateDoctor(1L, updatedDoctor));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//
//    // DELETE doctor
//    @Test
//    void testDeleteDoctor_Success() {
//        doNothing().when(doctorService).deleteDoctor(1L);
//
//        ResponseEntity<String> response = doctorController.deleteDoctor(1L);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Doctor deleted successfully", response.getBody());
//        verify(doctorService, times(1)).deleteDoctor(1L);
//    }
//
//    @Test
//    void testDeleteDoctor_NotFound() {
//        doThrow(new DoctorNotFoundException("Doctor not found with id: 1"))
//                .when(doctorService).deleteDoctor(1L);
//
//        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
//                () -> doctorController.deleteDoctor(1L));
//        assertEquals("Doctor not found with id: 1", exception.getMessage());
//    }
//}
