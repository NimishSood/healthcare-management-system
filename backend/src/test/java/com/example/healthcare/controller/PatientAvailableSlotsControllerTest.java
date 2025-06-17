package com.example.healthcare.controller;

import com.example.healthcare.service.*;
import com.example.healthcare.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

public class PatientAvailableSlotsControllerTest {

    private MockMvc mockMvc;

    @Mock private PatientService patientService;
    @Mock private AppointmentService appointmentService;
    @Mock private SecurityUtils securityUtils;
    @Mock private DoctorService doctorService;
    @Mock private DoctorScheduleService doctorScheduleService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
    }

    @Test
    void testGetAvailableSlots() throws Exception {
        LocalDate date = LocalDate.of(2024,4,15);
        when(doctorScheduleService.getAvailableSlots(eq(1L), eq(date), eq(Duration.ofMinutes(30))))
                .thenReturn(List.of(LocalTime.of(9,0), LocalTime.of(9,30)));

        mockMvc.perform(get("/patient/doctors/1/available-slots")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("09:00"))
                .andExpect(jsonPath("$[1]").value("09:30"));

        verify(doctorScheduleService).getAvailableSlots(1L, date, Duration.ofMinutes(30));
    }
}