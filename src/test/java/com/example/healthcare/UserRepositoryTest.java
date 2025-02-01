package com.example.healthcare;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.User;
import com.example.healthcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")  // Use test-specific properties
@TestPropertySource(locations = "file:///C:/Users/Nimish/Desktop/healthcare/src/test/resources/application-test.properties")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    @Transactional
    @Rollback(false)
    void setup() {
        // ✅ Clear all existing data and force flush
        userRepository.deleteAll();
        userRepository.flush();  // Critical for immediate execution

        doctor = new Doctor();
        doctor.setEmail("doctor@example.com");
        doctor.setPassword("password123");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setPhoneNumber("1234567890");
        doctor.setLicenseNumber("MD123456");
        doctor.setSpecialty("Cardiology");
        doctor.setYearsOfExperience(10);
        userRepository.save(doctor);

        patient = new Patient();
        patient.setEmail("patient@example.com");
        patient.setPassword("password456");
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setPhoneNumber("9876543210");
        patient.setInsuranceProvider("Blue Cross");
        userRepository.save(patient);

        userRepository.flush();  // Ensure setup data is committed
    }


    @Test
    @Transactional
    @Rollback(false) // Ensure changes persist
    void testFindDoctorByEmail() {
        Optional<User> foundDoctorOpt = userRepository.findByEmail("doctor@example.com");
        assertTrue(foundDoctorOpt.isPresent(), "Doctor should exist in the database");
        assertTrue(foundDoctorOpt.get() instanceof Doctor, "Retrieved user should be a Doctor");

        Doctor foundDoctor = (Doctor) foundDoctorOpt.get();
        assertEquals("John", foundDoctor.getFirstName());
        assertEquals("Cardiology", foundDoctor.getSpecialty());
    }

    @Test
    @Transactional
    @Rollback(false) // Ensure changes persist
    void testFindPatientByEmail() {
        Optional<User> foundPatientOpt = userRepository.findByEmail("patient@example.com");
        assertTrue(foundPatientOpt.isPresent(), "Patient should exist in the database");
        assertTrue(foundPatientOpt.get() instanceof Patient, "Retrieved user should be a Patient");

        Patient foundPatient = (Patient) foundPatientOpt.get();
        assertEquals("Jane", foundPatient.getFirstName());
        assertEquals("Blue Cross", foundPatient.getInsuranceProvider());
    }

    @Test
    @Transactional
    void testEmailUniqueness() {
        // ✅ Fresh context for this test
        userRepository.deleteAll();
        userRepository.flush();  // Ensure clean slate

        // ✅ First valid insert
        Doctor freshDoctor = new Doctor();
        freshDoctor.setEmail("doctor@example.com");
        freshDoctor.setPassword("password123");
        freshDoctor.setFirstName("John");
        freshDoctor.setLastName("Doe");
        freshDoctor.setPhoneNumber("1234567890");
        freshDoctor.setLicenseNumber("MD123456");
        freshDoctor.setSpecialty("Cardiology");
        freshDoctor.setYearsOfExperience(10);
        userRepository.save(freshDoctor);
        userRepository.flush();  // Force immediate insert

        // ✅ Attempt duplicate
        Doctor duplicateDoctor = new Doctor();
        duplicateDoctor.setEmail("doctor@example.com");
        duplicateDoctor.setPassword("anotherPass");
        duplicateDoctor.setFirstName("Mike");
        duplicateDoctor.setLastName("Hawk");
        duplicateDoctor.setPhoneNumber("1230007890");
        duplicateDoctor.setLicenseNumber("MD987654");
        duplicateDoctor.setSpecialty("Neurology");
        duplicateDoctor.setYearsOfExperience(8);

        // Validate constraint violation
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicateDoctor);
            userRepository.flush();  // Trigger database constraint check
        });
    }

    @Test
    @Transactional
    @Rollback(false)
    void testUpdateDoctorDetails() {
        Optional<User> foundDoctorOpt = userRepository.findByEmail("doctor@example.com");
        assertTrue(foundDoctorOpt.isPresent(), "Doctor should exist before updating");
        assertTrue(foundDoctorOpt.get() instanceof Doctor, "Retrieved user should be a Doctor");

        Doctor doctor = (Doctor) foundDoctorOpt.get();
        doctor.setSpecialty("Neurology");
        doctor.setYearsOfExperience(15);
        userRepository.save(doctor);

        Optional<User> updatedDoctorOpt = userRepository.findByEmail("doctor@example.com");
        assertTrue(updatedDoctorOpt.isPresent(), "Updated doctor should exist");
        assertTrue(updatedDoctorOpt.get() instanceof Doctor, "Updated user should be a Doctor");

        Doctor updatedDoctor = (Doctor) updatedDoctorOpt.get();
        assertEquals("Neurology", updatedDoctor.getSpecialty());
        assertEquals(15, updatedDoctor.getYearsOfExperience());
    }

    @Test
    @Transactional
    @Rollback(false)
    void testDeletePatient() {
        Optional<User> foundPatientOpt = userRepository.findByEmail("patient@example.com");
        assertTrue(foundPatientOpt.isPresent(), "Patient should exist before deletion");
        assertTrue(foundPatientOpt.get() instanceof Patient, "Retrieved user should be a Patient");

        userRepository.delete(foundPatientOpt.get());

        Optional<User> deletedPatientOpt = userRepository.findByEmail("patient@example.com");
        assertFalse(deletedPatientOpt.isPresent(), "Patient should not exist after deletion");
    }

    @Test
    @Transactional
    @Rollback(false)
    void testFetchNonExistentUser() {
        Optional<User> userOpt = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(userOpt.isPresent(), "User should not exist in the database");
    }
}
