package com.example.healthcare.repository.DoctorSchedule;

import com.example.healthcare.entity.DoctorSchedule.DoctorOneTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorOneTimeSlotRepository extends JpaRepository<DoctorOneTimeSlot, Long> {
    List<DoctorOneTimeSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<DoctorOneTimeSlot> findByDoctorId(Long doctorId);

    List<DoctorOneTimeSlot> findByDoctorIdAndDateBetween(Long doctorId, LocalDate start, LocalDate end);

    void deleteByDoctorId(Long doctorId);
}

