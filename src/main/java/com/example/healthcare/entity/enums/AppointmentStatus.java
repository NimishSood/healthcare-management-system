package com.example.healthcare.entity.enums;

public enum AppointmentStatus {
    BOOKED,
    CANCELED,
    COMPLETED,
    RESCHEDULED;

    public boolean isActive() {
        return this == BOOKED || this == RESCHEDULED;
    }
}