package com.example.healthcare.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Doctor")
@Getter
@Setter
public class Doctor extends User {

    @NotBlank
    private String licenseNumber;

    @NotBlank
    private String specialty;

    @PositiveOrZero
    private Integer yearsOfExperience;


}
