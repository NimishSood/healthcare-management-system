package com.example.healthcare.controller;

import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // GET all users
    @Test
    void testGetAllUsers_WhenUsersExist() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Alice");
        user1.setLastName("Aardvark");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Bob");
        user2.setLastName("Baker");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_WhenNoUsersExist() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).getAllUsers();
    }

    // GET a single user
    @Test
    void testGetUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Charlie");

        when(userService.getUser(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetUser_NotFound() {
        when(userService.getUser(1L))
                .thenThrow(new UserNotFoundException("User not found with id: 1"));

        // Direct call: exception will propagate since controller does not catch it.
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.getUser(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    // PUT update user
    @Test
    void testUpdateUser_Success() {
        User updated = new User();
        updated.setFirstName("Dave");
        updated.setLastName("Doe");
        updated.setEmail("dave@example.com");

        doNothing().when(userService).updateUser(1L, updated);

        ResponseEntity<String> response = userController.updateUser(1L, updated);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        verify(userService, times(1)).updateUser(1L, updated);
    }

    @Test
    void testUpdateUser_NotFound() {
        User updated = new User();
        updated.setFirstName("Dave");

        doThrow(new UserNotFoundException("User not found with id: 1"))
                .when(userService).updateUser(1L, updated);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.updateUser(1L, updated));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    // DELETE user
    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<String> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new UserNotFoundException("User not found with id: 1"))
                .when(userService).deleteUser(1L);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.deleteUser(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
    }
}
