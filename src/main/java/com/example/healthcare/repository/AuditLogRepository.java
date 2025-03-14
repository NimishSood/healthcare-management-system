package com.example.healthcare.repository;

import com.example.healthcare.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Finds logs by the user who performed the action.
     * @param performedBy The email of the user
     * @return List of audit logs
     */
    List<AuditLog> findByPerformedBy(String performedBy);

    /**
     * Finds logs by a specific action.
     * @param action The action performed (e.g., "Doctor Removed", "Admin Reactivated")
     * @return List of audit logs
     */
    List<AuditLog> findByAction(String action);

    /**
     * Finds logs within a specific date range.
     * @param startDate The start date
     * @param endDate The end date
     * @return List of logs within the given period
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}
