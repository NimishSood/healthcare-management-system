//package com.example.healthcare.service;
//
//import com.example.healthcare.entity.Admin;
//import com.example.healthcare.entity.Owner;
//import com.example.healthcare.entity.enums.UserRole;
//import com.example.healthcare.exception.AdminNotFoundException;
//import com.example.healthcare.exception.UnauthorizedAccessException;
//import com.example.healthcare.repository.AdminRepository;
//import com.example.healthcare.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class OwnerServiceTest {
//
//    @Mock
//    private AdminRepository adminRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private OwnerService ownerService;
//
//    private Owner validOwner;
//    private Owner invalidOwner; // Instance of Owner with a non-OWNER role
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        // Create a valid owner with role OWNER.
//        validOwner = new Owner();
//        validOwner.setId(1L);
//        validOwner.setEmail("owner@example.com");
//        validOwner.setRole(UserRole.OWNER);
//
//        // Create an "invalid" owner by setting its role to something other than OWNER.
//        invalidOwner = new Owner();
//        invalidOwner.setId(2L);
//        invalidOwner.setEmail("nonowner@example.com");
//        invalidOwner.setRole(UserRole.ADMIN);  // Not OWNER—even though it's an Owner instance.
//    }
//
//    @Test
//    public void testAddAdmin_Success() {
//        Admin newAdmin = new Admin();
//        newAdmin.setFirstName("John");
//        newAdmin.setLastName("Doe");
//        newAdmin.setEmail("admin@example.com");
//
//        ownerService.addAdmin(validOwner, newAdmin);
//        verify(adminRepository, times(1)).save(newAdmin);
//    }
//
//    @Test
//    public void testAddAdmin_Unauthorized() {
//        Admin newAdmin = new Admin();
//        newAdmin.setFirstName("John");
//        newAdmin.setLastName("Doe");
//        newAdmin.setEmail("admin@example.com");
//
//        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
//            ownerService.addAdmin(invalidOwner, newAdmin);
//        });
//        assertEquals("Only Owners can add Admins.", exception.getMessage());
//        verify(adminRepository, never()).save(any(Admin.class));
//    }
//
//    @Test
//    public void testRemoveAdmin_Success() {
//        // Suppose we're removing an admin with id 5.
//        Admin existingAdmin = new Admin();
//        existingAdmin.setId(5L);
//        when(adminRepository.findById(5L)).thenReturn(Optional.of(existingAdmin));
//
//        ownerService.removeAdmin(validOwner, 5L);
//        verify(adminRepository, times(1)).delete(existingAdmin);
//    }
//
//    @Test
//    public void testRemoveAdmin_AdminNotFound() {
//        when(adminRepository.findById(5L)).thenReturn(Optional.empty());
//        AdminNotFoundException exception = assertThrows(AdminNotFoundException.class, () -> {
//            ownerService.removeAdmin(validOwner, 5L);
//        });
//        assertEquals("Admin not found with id: 5", exception.getMessage());
//    }
//
//    @Test
//    public void testRemoveAdmin_Unauthorized() {
//        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
//            ownerService.removeAdmin(invalidOwner, 5L);
//        });
//        assertEquals("Only Owners can remove Admins.", exception.getMessage());
//        verify(adminRepository, never()).delete(any());
//    }
//}
