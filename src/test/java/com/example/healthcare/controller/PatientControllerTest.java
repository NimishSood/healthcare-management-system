package com.example.healthcare.controller;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    void testGetPatientById() throws Exception {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientService.getPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk());
    }
}