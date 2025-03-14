package com.example.healthcare.repository;

import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIsDeletedFalse();
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String mail);

    List<User> findByCreatedAtAfter(LocalDateTime thirtyDaysAgo);
    List<User> findByRoleAndIsDeletedFalse(UserRole role);
}