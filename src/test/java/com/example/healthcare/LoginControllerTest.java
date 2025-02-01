package com.example.healthcare;

import com.example.healthcare.entity.User;
import com.example.healthcare.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // Ensure database is clean before tests

        // Creating a test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123")); // Hash password
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhoneNumber("1234567890");

        userRepository.save(testUser);
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // Create login request
        String loginRequest = objectMapper.writeValueAsString(new LoginRequest("test@example.com", "password123"));

        // Perform login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("Login Successful"));
    }

    @Test
    void testInvalidPassword() throws Exception {
        // Create login request with wrong password
        String loginRequest = objectMapper.writeValueAsString(new LoginRequest("test@example.com", "wrongpassword"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized()) // Expect 401 Unauthorized
                .andExpect(jsonPath("$").value("Invalid credentials"));
    }

    @Test
    void testUserNotFound() throws Exception {
        // Attempt login with a non-existent user
        String loginRequest = objectMapper.writeValueAsString(new LoginRequest("notfound@example.com", "password123"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized()) // Expect 401 Unauthorized
                .andExpect(jsonPath("$").value("Invalid credentials"));
    }
}

// Create a helper DTO for login request
@Getter
class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

// Create a helper DTO for login response
class LoginResponse {
    private String email;
    private String message;

    public LoginResponse(String email, String message) {
        this.email = email;
        this.message = message;
    }
}
