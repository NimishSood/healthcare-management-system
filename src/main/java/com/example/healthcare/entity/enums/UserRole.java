package com.example.healthcare.entity.enums;

public enum UserRole {
    PATIENT,
    DOCTOR,
    ADMIN,
    OWNER;

    public boolean isAdminOrOwner() {
        return this == ADMIN || this == OWNER;
    }
}