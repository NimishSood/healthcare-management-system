package com.example.healthcare.entity.enums;

public enum AccountStatus {
    ACTIVE,
    DEACTIVATED,
    LOCKED;

    public boolean isActive()
    {
        return this == ACTIVE;
    }
}