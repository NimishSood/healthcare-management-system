package com.example.healthcare.repository;

import com.example.healthcare.entity.Notification;
import com.example.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Retrieve all notifications for a user.
    List<Notification> findByRecipient(User recipient);

    // Retrieve only unread notifications for a user.
    List<Notification> findByRecipientAndIsReadFalse(User recipient);
}
