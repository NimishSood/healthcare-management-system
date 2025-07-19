package com.example.healthcare.service;

import com.example.healthcare.entity.*;
import com.example.healthcare.repository.*;
import com.example.healthcare.entity.Attachment;
import com.example.healthcare.entity.enums.AttachmentParentType;
import com.example.healthcare.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AttachmentService attachmentService;

    @Transactional
    public LabResult createLabResult(Long patientId, Long doctorId, Long appointmentId,
                                     String testName, String resultValue, String units,
                                     String normalRange, String notes) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        Appointment appointment = null;
        if (appointmentId != null) {
            appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        }
        LabResult lr = new LabResult();
        lr.setPatient(patient);
        lr.setDoctor(doctor);
        lr.setAppointment(appointment);
        lr.setTestName(testName);
        lr.setResultValue(resultValue);
        lr.setUnits(units);
        lr.setNormalRange(normalRange);
        lr.setNotes(notes);
        return labResultRepository.save(lr);
    }

    public LabResult getLabResult(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lab result not found"));
    }

    public List<LabResult> getLabResultsForPatient(Long patientId) {
        return labResultRepository.findByPatientId(patientId);
    }

    @Transactional
    public LabResult updateLabResult(Long id, LabResult data) {
        LabResult lr = getLabResult(id);
        lr.setTestName(data.getTestName());
        lr.setResultValue(data.getResultValue());
        lr.setUnits(data.getUnits());
        lr.setNormalRange(data.getNormalRange());
        lr.setNotes(data.getNotes());
        return labResultRepository.save(lr);
    }

    public void deleteLabResult(Long id) {
        labResultRepository.deleteById(id);
        try {
            attachmentService.deleteAllForParent(AttachmentParentType.LAB_RESULT, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public Attachment addAttachment(Long labResultId, MultipartFile file) throws Exception {
        getLabResult(labResultId); // ensure exists and authorized
        return attachmentService.addAttachment(AttachmentParentType.LAB_RESULT, labResultId, file);
    }

    public List<Attachment> getAttachments(Long labResultId) {
        return attachmentService.listAttachments(AttachmentParentType.LAB_RESULT, labResultId);
    }

    public Attachment getAttachment(Long attachmentId) {
        return attachmentService.getAttachment(attachmentId);
    }

    public void deleteAttachment(Long attachmentId) throws Exception {
        attachmentService.deleteAttachment(attachmentId);
    }
}