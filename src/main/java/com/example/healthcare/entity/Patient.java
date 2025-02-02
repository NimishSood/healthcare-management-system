package com.example.healthcare.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Patient")
@Getter
@Setter
public class Patient extends User {

    @NotBlank
    private String insuranceProvider;

    private boolean isDeleted = false; // Soft deletion flag
}
