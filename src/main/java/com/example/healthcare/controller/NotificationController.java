package com.example.healthcare.controller;

import com.example.healthcare.entity.Notification;
import com.example.healthcare.entity.User;
import com.example.healthcare.service.NotificationService;
import com.example.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Endpoint to retrieve all notifications for a given user.
     * In a real app, the user should be obtained from the security context.
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Long userId) {
        User user = userService.getUser(userId);
        List<Notification> notifications = notificationService.getNotificationsForUser(user);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Endpoint to retrieve unread notifications for a given user.
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam Long userId) {
        User user = userService.getUser(userId);
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(user);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marks a notification as read.
     */
    @PostMapping("/{id}/markAsRead")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }

    /**
     * Marks all notifications for a user as read.
     */
    @PostMapping("/markAllAsRead")
    public ResponseEntity<String> markAllAsRead(@RequestParam Long userId) {
        User user = userService.getUser(userId);
        notificationService.markAllNotificationsAsRead(user);
        return ResponseEntity.ok("All notifications marked as read");
    }

    /**
     * Deletes a notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted");
    }
}
