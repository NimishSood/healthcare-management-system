package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.entity.enums.AppointmentStatus;
import com.example.healthcare.exception.AppointmentNotFoundException;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.repository.MessageRepository;
import com.example.healthcare.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final  DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MessageRepository messageRepository;
    private final AuditLogService auditLogService; // ✅ Injected Audit Log Service



    @Transactional
    public void updateDoctorProfile(Doctor doctor, Doctor updatedDoctor) {
        String previousData = doctor.toString();

        if (updatedDoctor.getFirstName() != null) {
            doctor.setFirstName(updatedDoctor.getFirstName());
        }
        if (updatedDoctor.getLastName() != null) {
            doctor.setLastName(updatedDoctor.getLastName());
        }
        if (updatedDoctor.getSpecialty() != null) {
            doctor.setSpecialty(updatedDoctor.getSpecialty());
        }
        if (updatedDoctor.getPhoneNumber() != null) {
            doctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        }

        doctorRepository.save(doctor);

        auditLogService.logAction(
                "Doctor Profile Updated", doctor.getEmail(), "DOCTOR",
                "Doctor ID: " + doctor.getId(), previousData, doctor.toString()
        );
    }

    @Transactional
    public void softDeleteDoctor(Doctor doctor) {
        doctor.setDeleted(true);
        doctorRepository.save(doctor);

        auditLogService.logAction(
                "Doctor Account Deleted", doctor.getEmail(), "DOCTOR",
                "Doctor ID: " + doctor.getId(), doctor.toString(), null
        );
    }

    public List<Appointment> getUpcomingAppointments(User doctor) {
        if (!(doctor instanceof Doctor)) {
            throw new UnauthorizedAccessException("Only doctors can view upcoming appointments.");
        }
        return appointmentRepository.findByDoctorAndAppointmentTimeAfterAndIsDeletedFalse(
                (Doctor) doctor, LocalDateTime.now());
    }

    public List<Appointment> getPastAppointments(User doctor) {
        if (!(doctor instanceof Doctor)) {
            throw new UnauthorizedAccessException("Only doctors can view past appointments.");
        }
        return appointmentRepository.findByDoctorAndAppointmentTimeBeforeAndIsDeletedFalse(
                (Doctor) doctor, LocalDateTime.now());
    }

    @Transactional
    public void markAppointmentComplete(Doctor doctor, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found."));

        if (!appointment.getDoctor().equals(doctor)) {
            throw new UnauthorizedAccessException("Doctors can only mark their own appointments.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        auditLogService.logAction(
                "Appointment Marked as Completed", doctor.getEmail(), "DOCTOR",
                "Appointment ID: " + appointmentId, "BOOKED", "COMPLETED"
        );
    }

    public List<?> getMessages(Long id) {
        return null;
    }

    public void sendMessage(Long id, Long patientId, String message) {
        auditLogService.logAction(
                "Message Sent", "Doctor ID: " + id, "DOCTOR",
                "Patient ID: " + patientId, null, "Message Sent"
        );
    }

    public void issuePrescription(Doctor doctor, Long patientId, String medicationDetails) {
        auditLogService.logAction(
                "Prescription Issued", doctor.getEmail(), "DOCTOR",
                "Patient ID: " + patientId, null, "Issued Prescription"
        );
    }

    public List<Doctor> getAllDoctors()
    {
        return doctorRepository.findAllByIsDeletedFalse();
    }

    public List<?> getPrescriptions(Long id)
    {
        return null;
    }



}
