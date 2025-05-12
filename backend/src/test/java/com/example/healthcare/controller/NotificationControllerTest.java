package com.example.healthcare.controller;

import com.example.healthcare.entity.Notification;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.NotificationType;
import com.example.healthcare.service.NotificationService;
import com.example.healthcare.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
    }

    @Test
    public void testGetNotifications() throws Exception {
        Notification n1 = new Notification();
        n1.setId(1L);
        n1.setMessage("Appointment reminder");
        n1.setType(NotificationType.APPOINTMENT);
        Notification n2 = new Notification();
        n2.setId(2L);
        n2.setMessage("Prescription approved");
        n2.setType(NotificationType.PRESCRIPTION);
        List<Notification> notifications = Arrays.asList(n1, n2);

        when(userService.getUser(testUser.getId())).thenReturn(testUser);
        when(notificationService.getNotificationsForUser(testUser)).thenReturn(notifications);

        mockMvc.perform(get("/notifications")
                        .param("userId", String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Appointment reminder")))
                .andExpect(content().string(containsString("Prescription approved")));
    }

    @Test
    public void testMarkNotificationAsRead() throws Exception {
        // Assume notification id 5 exists.
        doNothing().when(notificationService).markNotificationAsRead(5L);

        mockMvc.perform(post("/notifications/5/markAsRead"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Notification marked as read")));

        verify(notificationService, times(1)).markNotificationAsRead(5L);
    }

    @Test
    public void testMarkAllAsRead() throws Exception {
        doNothing().when(notificationService).markAllNotificationsAsRead(testUser);
        when(userService.getUser(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(post("/notifications/markAllAsRead")
                        .param("userId", String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("All notifications marked as read")));

        verify(notificationService, times(1)).markAllNotificationsAsRead(testUser);
    }

    @Test
    public void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(10L);

        mockMvc.perform(delete("/notifications/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Notification deleted")));

        verify(notificationService, times(1)).deleteNotification(10L);
    }
}
