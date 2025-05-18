package com.example.healthcare.repository;

import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIsDeletedFalse();

    Optional<User> findById(long id);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // ✅ Check if email already exists
    List<User> findByCreatedAtAfter(LocalDateTime thirtyDaysAgo);
    List<User> findByRoleAndIsDeletedFalse(UserRole role);

    // FIX: Use UserRole instead of String for role
    List<User> findAllByRole(UserRole role);

    // Custom query to find all users except the one with the given userId
    @Query("SELECT u FROM User u WHERE u.id <> :userId AND u.isDeleted = false")
    List<User> findAllExcept(@Param("userId") Long userId);


}
