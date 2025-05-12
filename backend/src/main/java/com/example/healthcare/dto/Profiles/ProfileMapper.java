package com.example.healthcare.dto.Profiles;

import com.example.healthcare.entity.*;

public
class ProfileMapper {

    public static OwnerProfileDto toOwnerDto(Owner owner) {
        OwnerProfileDto dto = new OwnerProfileDto();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setPhoneNumber(owner.getPhoneNumber());
        dto.setRole(owner.getRole().name());
        dto.setAccountStatus(owner.getAccountStatus().name());
        return dto;
    }

    public static AdminProfileDto toAdminDto(Admin admin) {
        AdminProfileDto dto = new AdminProfileDto();
        dto.setId(admin.getId());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setEmail(admin.getEmail());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setRole(admin.getRole().name());
        dto.setAccountStatus(admin.getAccountStatus().name());
        return dto;
    }

    public static DoctorProfileDto toDoctorDto(Doctor doctor) {
        DoctorProfileDto dto = new DoctorProfileDto();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setRole(doctor.getRole().name());
        dto.setAccountStatus(doctor.getAccountStatus().name());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        return dto;
    }

    public static PatientProfileDto toPatientDto(Patient patient) {
        PatientProfileDto dto = new PatientProfileDto();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setRole(patient.getRole().name());
        dto.setAccountStatus(patient.getAccountStatus().name());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        return dto;
    }
}
