//package com.example.healthcare.controller;
//
//import com.example.healthcare.entity.User;
//import com.example.healthcare.service.AuthService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthControllerTest {
//
//    @Mock
//    private AuthService authService;
//
//    @InjectMocks
//    private AuthController authController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test for registration endpoint.
//    @Test
//    void testRegister_Success() {
//        // Arrange: Create a dummy user.
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password123");
//
//        // When registerUser is called, do nothing (or verify later)
//        doNothing().when(authService).registerUser(user);
//
//        // Act: Call the registration endpoint.
//        ResponseEntity<String> response = authController.register(user);
//
//        // Assert: Check that the status is 201 and the body contains the success message.
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("User registered successfully", response.getBody());
//        verify(authService, times(1)).registerUser(user);
//    }
//
//    // Test for login endpoint when credentials are valid.
//    @Test
//    void testLogin_Success() {
//        // Arrange: Prepare a dummy user.
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("hashedPassword");  // Assume it's already hashed.
//
//        // When login is called with given email and password, return the user.
//        when(authService.login("test@example.com", "password123")).thenReturn(Optional.of(user));
//
//        // Act: Call the login endpoint.
//        ResponseEntity<String> response = authController.login("test@example.com", "password123");
//
//        // Assert: Verify the success response.
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Login successful", response.getBody());
//        verify(authService, times(1)).login("test@example.com", "password123");
//    }
//
//    // Test for login endpoint when credentials are invalid.
//    @Test
//    void testLogin_InvalidCredentials() {
//        // Arrange: When login is called, return an empty Optional.
//        when(authService.login("test@example.com", "wrongpassword")).thenReturn(Optional.empty());
//
//        // Act: Call the login endpoint.
//        ResponseEntity<String> response = authController.login("test@example.com", "wrongpassword");
//
//        // Assert: Verify the unauthorized response.
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals("Invalid credentials", response.getBody());
//        verify(authService, times(1)).login("test@example.com", "wrongpassword");
//    }
//}
