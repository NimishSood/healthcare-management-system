package com.example.healthcare.service;

import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // getAllUsers
    @Test
    void testGetAllUsers_WhenUsersExist() {
        User u1 = new User();
        u1.setId(1L);
        u1.setFirstName("Alice");

        User u2 = new User();
        u2.setId(2L);
        u2.setFirstName("Bob");

        List<User> users = Arrays.asList(u1, u2);
        when(userRepository.findAllByIsDeletedFalse()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }

    @Test
    void testGetAllUsers_WhenNoUsersExist() {
        when(userRepository.findAllByIsDeletedFalse()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAllByIsDeletedFalse();
    }

    // getUser
    @Test
    void testGetUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Charlie");

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    // updateUser
    @Test
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setId(1L);
        existing.setFirstName("David");
        existing.setLastName("Doe");
        existing.setEmail("david@example.com");

        User updated = new User();
        updated.setFirstName("Daniel");
        updated.setLastName("Duke");
        updated.setEmail("daniel@example.com");

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        userService.updateUser(1L, updated);
        assertEquals("Daniel", existing.getFirstName());
        assertEquals("Duke", existing.getLastName());
        assertEquals("daniel@example.com", existing.getEmail());
        verify(userRepository, times(1)).save(existing);
    }

    // deleteUser
    @Test
    void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(false);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUser(1L);
        assertTrue(user.isDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));
        assertEquals("User not found with id: 1", exception.getMessage());
    }
}
