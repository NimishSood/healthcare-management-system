package com.example.healthcare.entity.DoctorSchedule;

import com.example.healthcare.entity.Doctor;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class DoctorRecurringSchedule {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // java.time.DayOfWeek

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean active=true;


}
