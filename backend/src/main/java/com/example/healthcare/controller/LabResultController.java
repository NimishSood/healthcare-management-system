package com.example.healthcare.controller;

import com.example.healthcare.entity.LabResult;
import com.example.healthcare.service.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lab-results")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;

    @PostMapping
    public LabResult create(@RequestBody LabResult request) {
        return labResultService.createLabResult(
                request.getPatient().getId(),
                request.getDoctor().getId(),
                request.getAppointment() != null ? request.getAppointment().getId() : null,
                request.getTestName(),
                request.getResultValue(),
                request.getUnits(),
                request.getNormalRange(),
                request.getNotes()
        );
    }

    @GetMapping("/{id}")
    public LabResult get(@PathVariable Long id) {
        return labResultService.getLabResult(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<LabResult> forPatient(@PathVariable Long patientId) {
        return labResultService.getLabResultsForPatient(patientId);
    }

    @PutMapping("/{id}")
    public LabResult update(@PathVariable Long id, @RequestBody LabResult request) {
        return labResultService.updateLabResult(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        labResultService.deleteLabResult(id);
    }
}