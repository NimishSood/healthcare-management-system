package com.example.healthcare.controller;

import com.example.healthcare.dto.Prescription.PrescriptionRequestDto;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.Prescription;
import com.example.healthcare.entity.User;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final SecurityUtils securityUtils;  // Utility to get current user/doctor/patient

    /** POST /prescriptions - Create a new prescription (Doctors only) */
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> createPrescription(@RequestBody PrescriptionRequestDto dto) {
        // Get the authenticated doctor entity
        Doctor doctor = securityUtils.getAuthenticatedDoctor();

        // Use fields from the DTO
        prescriptionService.createPrescription(
                doctor,
                dto.getPatientId(),
                dto.getAppointmentId(),
                dto.getMedicationName(),
                dto.getDosage(),
                dto.getInstructions(),
                dto.getRefillsLeft()
        );
        return ResponseEntity.ok("Prescription issued successfully.");
    }

    /** GET /prescriptions/{id} - Get a prescription by its ID (Doctor, Patient, Admin/Owner) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','PATIENT','ADMIN','OWNER')")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id) {
        User currentUser = securityUtils.getAuthenticatedUser(); // gets current User (could be Doctor, Patient, etc.)
        Prescription prescription = prescriptionService.getPrescriptionById(id, currentUser);
        return ResponseEntity.ok(prescription);
    }

    /** GET /prescriptions/patient/{patientId} - List prescriptions for a patient (Doctor or Admin/Owner, or patient themselves) */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','OWNER','PATIENT')")
    public ResponseEntity<List<Prescription>> getPrescriptionsForPatient(@PathVariable Long patientId) {
        User currentUser = securityUtils.getAuthenticatedUser();
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId, currentUser);
        return ResponseEntity.ok(prescriptions);
    }

    /** DELETE /prescriptions/{id} - Cancel (soft-delete) a prescription (Doctor or Admin/Owner) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','OWNER')")
    public ResponseEntity<Void> cancelPrescription(@PathVariable Long id) {
        User currentUser = securityUtils.getAuthenticatedUser();
        prescriptionService.cancelPrescription(id, currentUser);
        // Return 204 No Content to indicate success without response body
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyPrescriptions() {
        User user = securityUtils.getAuthenticatedUser();
        if (!(user instanceof Patient)) {
            return ResponseEntity.badRequest().body("Authenticated user is not a patient.");
        }
        Patient patient = (Patient) user;
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patient.getId(), patient);
        return ResponseEntity.ok(prescriptions);
    }


    // Patient requests a refill
    @PostMapping("/{id}/refill-request")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> requestRefill(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User authorities: ");
        for (GrantedAuthority ga : auth.getAuthorities()) {
            System.out.println(ga.getAuthority());
        }
        User user = securityUtils.getAuthenticatedUser();
        prescriptionService.requestRefill(id, user);
        return ResponseEntity.ok("Refill request submitted.");
    }

    // Doctor approves/denies refill
    @PatchMapping("/{id}/refill-approve")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> approveRefill(
            @PathVariable Long id,
            @RequestParam("approve") boolean approve
    ) {
        Doctor doctor = securityUtils.getAuthenticatedDoctor();
        prescriptionService.approveRefill(id, doctor, approve);
        return ResponseEntity.ok(approve ? "Refill approved." : "Refill denied.");
    }


}
