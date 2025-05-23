package com.example.healthcare.repository.DoctorSchedule;

import com.example.healthcare.entity.DoctorSchedule.SlotRemovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlotRemovalRequestRepository extends JpaRepository<SlotRemovalRequest, Long> {
    // Find all requests by doctor
    List<SlotRemovalRequest> findByDoctor_Id(Long doctorId);

    // Find all pending requests (for admin review)
    List<SlotRemovalRequest> findByStatus(String status);

    // Find pending requests for a given doctor/slot type/slotId
    boolean existsByDoctor_IdAndSlotTypeAndSlotIdAndStatus(Long doctorId, String slotType, Long slotId, String status);

}
