package com.example.healthcare.service;

import com.example.healthcare.entity.Notification;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.NotificationType;
import com.example.healthcare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Creates and sends a notification to a recipient.
     */
    public Notification sendNotification(User recipient, String message, NotificationType type, String additionalData) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setAdditionalData(additionalData);
        // createdAt is set automatically via @PrePersist
        return notificationRepository.save(notification);
    }

    /**
     * Retrieves all notifications for a given user.
     */
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipient(user);
    }

    /**
     * Retrieves only unread notifications for a given user.
     */
    public List<Notification> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByRecipientAndIsReadFalse(user);
    }

    /**
     * Marks a specific notification as read.
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Marks all notifications for a user as read.
     */
    @Transactional
    public void markAllNotificationsAsRead(User user) {
        List<Notification> unread = notificationRepository.findByRecipientAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Deletes a notification.
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
