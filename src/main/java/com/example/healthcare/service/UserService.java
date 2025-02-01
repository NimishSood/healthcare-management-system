package com.example.healthcare.service;

import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UserNotFoundException;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAllByIsDeletedFalse();
    }

    public void updateUser(Long id, User updated) {
        User existing = getUser(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        User user = getUser(id);
        user.setDeleted(true); // Soft deletion
        userRepository.save(user);
    }

    public List<User> getUsersCreatedLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.findByCreatedAtAfter(thirtyDaysAgo);
    }

}