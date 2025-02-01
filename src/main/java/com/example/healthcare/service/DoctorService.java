package com.example.healthcare.service;

import com.example.healthcare.entity.Doctor;
import com.example.healthcare.exception.DoctorNotFoundException;
import com.example.healthcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllByIsDeletedFalse();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
    }

    public void createDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    public void updateDoctor(Long id, Doctor updatedDoc) {
        Doctor existing = getDoctorById(id);
        existing.setFirstName(updatedDoc.getFirstName());
        existing.setLastName(updatedDoc.getLastName());
        existing.setSpecialty(updatedDoc.getSpecialty());
        existing.setLicenseNumber(updatedDoc.getLicenseNumber());
        existing.setYearsOfExperience(updatedDoc.getYearsOfExperience());
        doctorRepository.save(existing);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctor.setDeleted(true); // Soft deletion
        doctorRepository.save(doctor);
    }
}