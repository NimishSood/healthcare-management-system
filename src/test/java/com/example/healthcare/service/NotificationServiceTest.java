package com.example.healthcare.service;

import com.example.healthcare.entity.Notification;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.NotificationType;
import com.example.healthcare.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
    }

    @Test
    public void testSendNotification() {
        Notification notification = new Notification();
        notification.setRecipient(testUser);
        notification.setMessage("Your appointment is tomorrow.");
        notification.setType(NotificationType.APPOINTMENT);
        // When saving, simulate that the notification gets an ID.
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(100L);
            n.setCreatedAt(LocalDateTime.now());
            return n;
        });

        Notification result = notificationService.sendNotification(testUser, "Your appointment is tomorrow.", NotificationType.APPOINTMENT, null);
        assertNotNull(result.getId());
        assertEquals("Your appointment is tomorrow.", result.getMessage());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testGetNotificationsForUser() {
        Notification n1 = new Notification();
        n1.setMessage("Message 1");
        Notification n2 = new Notification();
        n2.setMessage("Message 2");

        List<Notification> notifications = Arrays.asList(n1, n2);
        when(notificationRepository.findByRecipient(testUser)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsForUser(testUser);
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByRecipient(testUser);
    }

    @Test
    public void testMarkNotificationAsRead() {
        Notification notification = new Notification();
        notification.setId(200L);
        notification.setRead(false);
        when(notificationRepository.findById(200L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        notificationService.markNotificationAsRead(200L);
        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testMarkAllNotificationsAsRead() {
        Notification n1 = new Notification();
        n1.setId(1L);
        n1.setRead(false);
        Notification n2 = new Notification();
        n2.setId(2L);
        n2.setRead(false);
        List<Notification> unreadNotifications = Arrays.asList(n1, n2);
        when(notificationRepository.findByRecipientAndIsReadFalse(testUser)).thenReturn(unreadNotifications);

        notificationService.markAllNotificationsAsRead(testUser);
        assertTrue(n1.isRead());
        assertTrue(n2.isRead());
        verify(notificationRepository, times(1)).saveAll(unreadNotifications);
    }

    @Test
    public void testDeleteNotification() {
        Long notificationId = 300L;
        notificationService.deleteNotification(notificationId);
        verify(notificationRepository, times(1)).deleteById(notificationId);
    }
}
