package com.example.healthcare.repository.DoctorSchedule;

import com.example.healthcare.entity.DoctorSchedule.DoctorRecurringBreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRecurringBreakRepository extends JpaRepository<DoctorRecurringBreak, Long> {
    List<DoctorRecurringBreak> findByDoctorId(Long doctorId);
}
