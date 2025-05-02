package com.example.healthcare.service;

// File: AdminService.java


import com.example.healthcare.entity.*;
import java.util.List;

public interface AdminService {
    void addDoctor(User admin, Doctor doctor);
    void addPatient(User admin, Patient patient);
    void removeDoctor(User admin, Long doctorId);
    void removePatient(User admin, Long patientId);
    List<Doctor> getAllDoctors(User admin);
    List<Patient> getAllPatients(User admin);
    List<Appointment> getAllAppointments(User admin);
    void reactivateDoctor(User admin, Long doctorId);
    void updateAdminProfile(Long adminId, Admin updatedAdmin);
    void softDeleteDoctor(Admin admin, Long doctorId);
    void softDeletePatient(Admin admin, Long patientId);
    List<AuditLog> getAuditLogs(Admin admin);
}


