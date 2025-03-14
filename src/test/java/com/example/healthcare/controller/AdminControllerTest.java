package com.example.healthcare.controller;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.service.AdminService;
import com.example.healthcare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {

    // Remove @Autowired since we're manually creating MockMvc
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private User adminUser;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        // Build a standalone MockMvc instance for AdminController.
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        // Create a sample admin user.
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(UserRole.ADMIN);
    }

    // --- Test for adding a Doctor ---

    @Test
    public void testAddDoctor_Success() throws Exception {
        // Optionally set up a mocked Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(adminUser.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When the controller calls userService.getUser(adminUser.getId()), return the admin user.
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        // Prepare JSON content representing a new Doctor.
        String doctorJson = "{ \"firstName\": \"Doc\", \"lastName\": \"Tor\", \"email\": \"doctor@example.com\" }";

        mockMvc.perform(post("/admin/add-doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorJson)
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Doctor added successfully.")));

        // Verify that adminService.addDoctor was called exactly once with the expected parameters.
        verify(adminService, times(1)).addDoctor(eq(adminUser), any(Doctor.class));
    }

    @Test
    public void testAddDoctor_Unauthorized() throws Exception {
        // Simulate a non-admin user.
        User nonAdmin = new User();
        nonAdmin.setId(2L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        String doctorJson = "{ \"firstName\": \"Doc\", \"lastName\": \"Tor\", \"email\": \"doctor@example.com\" }";

        mockMvc.perform(post("/admin/add-doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(doctorJson)
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).addDoctor(any(), any(Doctor.class));
    }

    // --- Test for adding a Patient ---

    @Test
    public void testAddPatient_Success() throws Exception {
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        String patientJson = "{ \"firstName\": \"Pat\", \"lastName\": \"Ient\", \"email\": \"patient@example.com\" }";

        mockMvc.perform(post("/admin/add-patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Patient added successfully.")));

        verify(adminService, times(1)).addPatient(eq(adminUser), any(Patient.class));
    }

    @Test
    public void testAddPatient_Unauthorized() throws Exception {
        User nonAdmin = new User();
        nonAdmin.setId(3L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        String patientJson = "{ \"firstName\": \"Pat\", \"lastName\": \"Ient\", \"email\": \"patient@example.com\" }";

        mockMvc.perform(post("/admin/add-patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).addPatient(any(), any(Patient.class));
    }

    // --- Test for removing a Doctor ---

    @Test
    public void testRemoveDoctor_Success() throws Exception {
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        mockMvc.perform(delete("/admin/remove-doctor")
                        .param("doctorId", "10")
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Doctor removed successfully.")));

        verify(adminService, times(1)).removeDoctor(eq(adminUser), eq(10L));
    }

    @Test
    public void testRemoveDoctor_Unauthorized() throws Exception {
        User nonAdmin = new User();
        nonAdmin.setId(4L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        mockMvc.perform(delete("/admin/remove-doctor")
                        .param("doctorId", "10")
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).removeDoctor(any(), any(Long.class));
    }

    // --- Test for removing a Patient ---

    @Test
    public void testRemovePatient_Success() throws Exception {
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        mockMvc.perform(delete("/admin/remove-patient")
                        .param("patientId", "20")
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Patient removed successfully.")));

        verify(adminService, times(1)).removePatient(eq(adminUser), eq(20L));
    }

    @Test
    public void testRemovePatient_Unauthorized() throws Exception {
        User nonAdmin = new User();
        nonAdmin.setId(5L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        mockMvc.perform(delete("/admin/remove-patient")
                        .param("patientId", "20")
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).removePatient(any(), any(Long.class));
    }

    // --- Test for viewing Doctors ---

    @Test
    public void testViewDoctors_Success() throws Exception {
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        // Create a dummy list of doctors.
        Doctor doctor1 = new Doctor();
        doctor1.setFirstName("Doc1");
        Doctor doctor2 = new Doctor();
        doctor2.setFirstName("Doc2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);

        when(adminService.getAllDoctors(adminUser)).thenReturn(doctorList);

        mockMvc.perform(get("/admin/view-doctors")
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Doc1")))
                .andExpect(content().string(containsString("Doc2")));
    }

    @Test
    public void testViewDoctors_Unauthorized() throws Exception {
        User nonAdmin = new User();
        nonAdmin.setId(6L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        mockMvc.perform(get("/admin/view-doctors")
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllDoctors(any());
    }

    // --- Test for viewing Patients ---

    @Test
    public void testViewPatients_Success() throws Exception {
        when(userService.getUser(adminUser.getId())).thenReturn(adminUser);

        // Create a dummy list of patients.
        Patient patient1 = new Patient();
        patient1.setFirstName("Pat1");
        Patient patient2 = new Patient();
        patient2.setFirstName("Pat2");
        List<Patient> patientList = Arrays.asList(patient1, patient2);

        when(adminService.getAllPatients(adminUser)).thenReturn(patientList);

        mockMvc.perform(get("/admin/view-patients")
                        .param("adminId", String.valueOf(adminUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pat1")))
                .andExpect(content().string(containsString("Pat2")));
    }

    @Test
    public void testViewPatients_Unauthorized() throws Exception {
        User nonAdmin = new User();
        nonAdmin.setId(7L);
        nonAdmin.setEmail("nonadmin@example.com");
        nonAdmin.setRole(UserRole.PATIENT);

        when(userService.getUser(nonAdmin.getId())).thenReturn(nonAdmin);

        mockMvc.perform(get("/admin/view-patients")
                        .param("adminId", String.valueOf(nonAdmin.getId())))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllPatients(any());
    }
}
