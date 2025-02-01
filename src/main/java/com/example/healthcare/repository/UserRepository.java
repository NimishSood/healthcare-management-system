package com.example.healthcare.repository;

import com.example.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIsDeletedFalse();
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String mail);
}