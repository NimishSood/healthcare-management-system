package com.example.healthcare.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Admin")
@Getter
@Setter
public class Admin extends User {
    // No additional fields needed; Admin inherits all necessary properties from User.
}
