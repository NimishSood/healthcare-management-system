package com.example.healthcare.entity;

import com.example.healthcare.entity.enums.AccountStatus;
import com.example.healthcare.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.PATIENT; // Default role

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE; // Default status

    private boolean isDeleted = false; // Soft deletion flag

    public String getDtype() {
        return this.getClass().getAnnotation(DiscriminatorValue.class) != null ?
                this.getClass().getAnnotation(DiscriminatorValue.class).value() :
                "USER";
    }
}