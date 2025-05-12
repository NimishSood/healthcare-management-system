package com.example.healthcare;

import com.example.healthcare.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandler.class) // Explicitly import the handler
@SpringBootApplication
public class HealthcareApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareApplication.class, args);
    }
}
