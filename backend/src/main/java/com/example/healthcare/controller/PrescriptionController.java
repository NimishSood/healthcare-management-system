package com.example.healthcare.controller;

import com.example.healthcare.dto.Prescription.PrescriptionRequestDto;
import com.example.healthcare.entity.Doctor;
import com.example.healthcare.entity.Patient;
import com.example.healthcare.entity.Prescription;
import com.example.healthcare.entity.User;
import com.example.healthcare.security.SecurityUtils;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.service.PrescriptionService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.healthcare.dto.Prescription.PrescriptionDto;
import com.example.healthcare.dto.Prescription.PrescriptionMapper;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final SecurityUtils securityUtils;  // Utility to get current user/doctor/patient
    private final UserService userService;

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
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable Long id) {
        User currentUser = securityUtils.getAuthenticatedUser();
        Prescription prescription = prescriptionService.getPrescriptionById(id, currentUser);
        return ResponseEntity.ok(PrescriptionMapper.toDto(prescription));
    }

    /** GET /prescriptions/patient/{patientId} - List prescriptions for a patient (Doctor or Admin/Owner, or patient themselves) */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','OWNER','PATIENT')")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsForPatient(@PathVariable Long patientId) {
        User currentUser = securityUtils.getAuthenticatedUser();
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId, currentUser);
        List<PrescriptionDto> dtos = prescriptions.stream()
                .map(PrescriptionMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
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
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','DOCTOR','OWNER')")
    public ResponseEntity<?> getMyPrescriptions(
            @RequestParam(value = "patientId", required = false) Long patientId
    ) {
        // 1) Determine caller’s role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                        a.getAuthority().equals("ROLE_OWNER"));
        boolean isDoctor= auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        // 2) Figure out effective patientId and currentUser
        Long effectiveId;
        User effectiveUser;

        User caller = securityUtils.getAuthenticatedUser();

        if (patientId != null && (isAdmin || isDoctor)) {
            // admins and doctors can specify any patient
            effectiveId   = patientId;
            // load the actual User (must be Patient) for service checks
            User fetched = userService.getUserById(effectiveId);
            if (!(fetched instanceof Patient)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No patient found with id " + effectiveId);
            }
            effectiveUser = fetched;
        } else {
            // patients always use their own
            if (!(caller instanceof Patient)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Only patients may omit patientId.");
            }
            effectiveUser = caller;
            effectiveId   = ((Patient) caller).getId();
        }

        // 3) Call service with both parameters
        List<Prescription> prescriptions =
                prescriptionService.getPrescriptionsByPatient(effectiveId, effectiveUser);

        List<PrescriptionDto> dtos = prescriptions.stream()
                .map(PrescriptionMapper::toDto)
                .toList();


        // 4) Return the list
        return ResponseEntity.ok(dtos);
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
