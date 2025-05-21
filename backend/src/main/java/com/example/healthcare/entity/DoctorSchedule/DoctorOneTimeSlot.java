package com.example.healthcare.entity.DoctorSchedule;

import com.example.healthcare.entity.Doctor;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class DoctorOneTimeSlot {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    private LocalDate date;          // The calendar date of the override
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;       // true = extra working time, false = block/unavailable

    // Getters, setters, etc.
}

