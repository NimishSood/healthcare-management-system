package com.example.healthcare.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("Owner")
@Getter
@Setter
public class Owner extends User {


}
