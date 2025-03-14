package com.example.healthcare.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Message {
    @Id
    private Long id;

    
}
