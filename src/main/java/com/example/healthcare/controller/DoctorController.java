package com.example.healthcare.controller;

import com.example.healthcare.entity.Appointment;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    // ✅ View Doctor Profile
    @GetMapping("/profile")
    public Doctor getDoctorProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            return (Doctor) doctor;
        }
        throw new UnauthorizedAccessException("Only Doctors can view their profile.");
    }

    // ✅ Update Profile
    @PutMapping("/profile")
    public String updateDoctorProfile(@RequestBody Doctor updatedDoctor,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            doctorService.updateDoctorProfile((Doctor) doctor, updatedDoctor);
            return "Doctor profile updated successfully.";
        }
        throw new UnauthorizedAccessException("Only Doctors can edit their profile.");
    }

    // ✅ Soft Delete Account
    @DeleteMapping("/delete-account")
    public String deleteDoctor(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            doctorService.softDeleteDoctor((Doctor) doctor);
            return "Doctor account deleted successfully.";
        }
        throw new UnauthorizedAccessException("Only Doctors can delete their account.");
    }

    // ✅ View Assigned Appointments (Upcoming)
    @GetMapping("/appointments/upcoming")
    public List<Appointment> getUpcomingAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            return appointmentService.getUpcomingAppointments(doctor);
        }
        throw new UnauthorizedAccessException("Only Doctors can view their upcoming appointments.");
    }

    // ✅ View Past Appointments
    @GetMapping("/appointments/history")
    public List<Appointment> getPastAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            return appointmentService.getPastAppointments(doctor);
        }
        throw new UnauthorizedAccessException("Only Doctors can view past appointments.");
    }

    // ✅ Mark Appointment as Completed
    @PutMapping("/appointments/{appointmentId}/mark-complete")
    public String markAppointmentComplete(@PathVariable Long appointmentId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            appointmentService.markAppointmentComplete(doctor.getId(), appointmentId);
            return "Appointment marked as completed.";
        }
        throw new UnauthorizedAccessException("Only Doctors can mark appointments as completed.");
    }


    // ✅ View Messages with Patients
    @GetMapping("/messages")
    public List<?> getMessages(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            return doctorService.getMessages(doctor.getId());
        }
        throw new UnauthorizedAccessException("Only Doctors can view messages.");
    }

    // ✅ Send Message to a Patient
    @PostMapping("/send-message")
    public String sendMessage(@RequestParam Long patientId,
                              @RequestBody String message,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            doctorService.sendMessage(doctor.getId(), patientId, message);
            return "Message sent successfully.";
        }
        throw new UnauthorizedAccessException("Only Doctors can send messages.");
    }

    // ✅ View Prescriptions Issued
    @GetMapping("/prescriptions")
    public List<?> getPrescriptions(@AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            return doctorService.getPrescriptions(doctor.getId());
        }
        throw new UnauthorizedAccessException("Only Doctors can view prescriptions.");
    }

    // ✅ Issue a New Prescription
    @PostMapping("/issue-prescription")
    public String issuePrescription(@RequestParam Long patientId,
                                    @RequestParam String medicationDetails,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User doctor = userService.getUserByEmail(userDetails.getUsername());

        if (doctor instanceof Doctor) {
            doctorService.issuePrescription((Doctor) doctor, patientId, medicationDetails);
            return "Prescription issued successfully.";
        }
        throw new UnauthorizedAccessException("Only Doctors can issue prescriptions.");
    }
}
