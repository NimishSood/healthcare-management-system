package com.example.healthcare.service;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.exception.PatientNotFoundException;
import com.example.healthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAllByIsDeletedFalse();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
    }

    public void createPatient(Patient patient) {
        patientRepository.save(patient);
    }

    public void updatePatient(Long id, Patient updatedPatient) {
        Patient existing = getPatientById(id);
        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setPhoneNumber(updatedPatient.getPhoneNumber());
        existing.setInsuranceProvider(updatedPatient.getInsuranceProvider());
        patientRepository.save(existing);
    }

    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patient.setDeleted(true); // Soft deletion
        patientRepository.save(patient);
    }
}