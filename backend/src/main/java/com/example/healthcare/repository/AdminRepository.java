package com.example.healthcare.repository;

import com.example.healthcare.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByIdAndIsDeletedFalse(Long id);

    List<Admin> findAllByIsDeletedFalse();
}
