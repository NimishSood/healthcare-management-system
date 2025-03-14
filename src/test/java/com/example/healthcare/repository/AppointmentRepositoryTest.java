package com.example.healthcare.repository;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "doctor" + UUID.randomUUID().toString().substring(0, 5) + "@example.com";

        // Create and save a doctor with unique email
        doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setEmail(uniqueEmail); // ✅ Ensure a unique email
        doctor.setLicenseNumber("L" + UUID.randomUUID().toString().substring(0, 8));
        doctor.setSpecialty("Cardiology");
        doctor.setYearsOfExperience(10);
        doctor.setPhoneNumber("123-456-" + (int) (Math.random() * 10000)); // ✅ Unique phone number
        doctor.setPassword("hashed_password"); // ✅ Required field
        doctor = doctorRepository.save(doctor);

        // Create and save a patient with unique email
        String patientEmail = "patient" + UUID.randomUUID().toString().substring(0, 5) + "@example.com";

        patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Smith");
        patient.setEmail(patientEmail); // ✅ Ensure a unique email
        patient.setInsuranceProvider("ABC Insurance");
        patient.setPhoneNumber("987-654-" + (int) (Math.random() * 10000)); // ✅ Unique phone number
        patient.setPassword("hashed_password"); // ✅ Required field
        patient = patientRepository.save(patient);
    }

    @Test
    void testCreateAppointment_DefaultIsDeletedFalse() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.BOOKED);

        // Act
        Appointment saved = appointmentRepository.save(appointment);

        // Assert
        assertNotNull(saved);
        assertFalse(saved.isDeleted(), "Default value of isDeleted should be false");
    }

    @Test
    void testSoftDeleteAppointment() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment = appointmentRepository.save(appointment);

        // Act
        appointment.setDeleted(true);
        appointmentRepository.save(appointment);

        // Assert
        Optional<Appointment> deletedAppointment = appointmentRepository.findById(appointment.getId());
        assertTrue(deletedAppointment.isPresent());
        assertTrue(deletedAppointment.get().isDeleted(), "Appointment should be soft deleted");
    }
}
