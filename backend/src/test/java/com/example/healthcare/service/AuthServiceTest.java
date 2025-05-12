//package com.example.healthcare.service;
//
//import com.example.healthcare.entity.User;
//import com.example.healthcare.entity.enums.UserRole;
//import com.example.healthcare.exception.PatientNotFoundException;
//import com.example.healthcare.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private AuthService authService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test for user registration. Ensure that the password is hashed and role is set.
//    @Test
//    void testRegisterUser_SetsHashedPasswordAndDefaultRole() {
//        // Arrange
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("plainPassword");
//        user.setRole(null); // Role not provided.
//
//        // Simulate hashing behavior.
//        String hashedPassword = "hashedPassword123";
//        when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);
//
//        // When saving, just return the same user.
//        when(userRepository.save(user)).thenReturn(user);
//
//        // Act
//        authService.registerUser(user);
//
//        // Assert: Verify that the user's password is replaced with the hashed version.
//        assertEquals(hashedPassword, user.getPassword());
//        // Also, check that a default role is set (assuming default is PATIENT).
//        assertEquals(UserRole.PATIENT, user.getRole());
//        verify(passwordEncoder, times(1)).encode("plainPassword");
//        verify(userRepository, times(1)).save(user);
//    }
//
//    // Test login when the user exists and the password matches.
//    @Test
//    void testLogin_Success() {
//        // Arrange
//        String email = "test@example.com";
//        String rawPassword = "password123";
//        String hashedPassword = "hashedPassword123";
//
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword(hashedPassword);
//
//        // Simulate repository call.
//        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
//        // Simulate password check.
//        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
//
//        // Act
//        Optional<User> result = authService.login(email, rawPassword);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(email, result.get().getEmail());
//        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(email);
//        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
//    }
//
//    // Test login when the user exists but the password does not match.
//    @Test
//    void testLogin_InvalidPassword() {
//        // Arrange
//        String email = "test@example.com";
//        String rawPassword = "wrongPassword";
//        String hashedPassword = "hashedPassword123";
//
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword(hashedPassword);
//
//        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
//        // Simulate a password mismatch.
//        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);
//
//        // Act
//        Optional<User> result = authService.login(email, rawPassword);
//
//        // Assert
//        assertFalse(result.isPresent());
//        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(email);
//        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
//    }
//
//    // Test login when the user is not found.
//    @Test
//    void testLogin_UserNotFound() {
//        // Arrange
//        String email = "nonexistent@example.com";
//        String rawPassword = "password123";
//
//        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.empty());
//
//        // Act
//        Optional<User> result = authService.login(email, rawPassword);
//
//        // Assert
//        assertFalse(result.isPresent());
//        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(email);
//        // No password check should be invoked if the user is not found.
//        verify(passwordEncoder, never()).matches(anyString(), anyString());
//    }
//}
