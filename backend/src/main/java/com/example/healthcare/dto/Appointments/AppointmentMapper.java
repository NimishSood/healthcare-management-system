package com.example.healthcare.dto.Appointments;

import com.example.healthcare.entity.Appointment;

import java.util.List;
import java.util.stream.Collectors;

public class AppointmentMapper {
    public static AppointmentDto toDto(Appointment appt) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appt.getId());

        // doctor
        String dname = appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName();
        dto.setDoctorName(dname);
        dto.setSpecialty(appt.getDoctor().getSpecialty());
        dto.setDoctorContact(appt.getDoctor().getEmail() /* or phoneNumber */);

        // patient
        String pname = appt.getPatient().getFirstName() + " " + appt.getPatient().getLastName();
        dto.setPatientName(pname);

        // times & status
        dto.setAppointmentTime(appt.getAppointmentTime());
        dto.setStatus(appt.getStatus().name());

        // audit fields
        dto.setCreatedAt(appt.getCreatedAt());
        dto.setUpdatedAt(appt.getUpdatedAt());

        // cancellation
        if (appt.getCancelledBy() != null) {
            String who = appt.getCancelledBy().getFirstName()
                    + " " + appt.getCancelledBy().getLastName();
            dto.setCancelledByName(who);
        }

        dto.setLocation(appt.getLocation());
        dto.setNotes(appt.getNotes());

        return dto;
    }
}
