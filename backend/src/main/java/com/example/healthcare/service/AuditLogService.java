package com.example.healthcare.service;

import com.example.healthcare.entity.AuditLog;
import com.example.healthcare.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Logs an action performed by a user.
     *
     * @param action The action performed (e.g., "Admin Added", "Profile Updated")
     * @param performedBy The email of the user who performed the action
     * @param role The role of the user (Owner, Admin, Doctor, Patient)
     * @param affectedEntity The entity affected (e.g., "Doctor ID: 5")
     * @param previousData The previous state of the data (if applicable)
     * @param newData The new state of the data (if applicable)
     */
    @Transactional
    public void logAction(String action, String performedBy, String role, String affectedEntity, String previousData, String newData) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setRole(role);
        log.setAffectedEntity(affectedEntity);
        log.setPreviousData(previousData);
        log.setNewData(newData);
        log.setTimestamp(LocalDateTime.now()); // ✅ Automatically sets timestamp
        auditLogRepository.save(log);
    }

    /**
     * Retrieves all audit logs.
     * @return List of all audit logs
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * Retrieves logs for a specific user.
     * @param performedBy The email of the user whose logs should be retrieved
     * @return List of logs related to this user
     */
    public List<AuditLog> getLogsByUser(String performedBy) {
        return auditLogRepository.findByPerformedBy(performedBy);
    }

    /**
     * Retrieves logs for a specific action.
     * @param action The action performed (e.g., "Doctor Removed", "Admin Reactivated")
     * @return List of logs related to this action
     */
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    /**
     * Retrieves logs within a specific date range.
     * @param startDate The start date
     * @param endDate The end date
     * @return List of logs within the given period
     */
    public List<AuditLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }
}
