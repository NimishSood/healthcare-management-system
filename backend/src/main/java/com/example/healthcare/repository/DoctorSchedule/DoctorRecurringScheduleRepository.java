package com.example.healthcare.repository.DoctorSchedule;


import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface DoctorRecurringScheduleRepository extends JpaRepository<DoctorRecurringSchedule, Long> {
    List<DoctorRecurringSchedule> findByDoctorId(Long doctorId);
    // Add custom methods for overlap checks if needed
}

