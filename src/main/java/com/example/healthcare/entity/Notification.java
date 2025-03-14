package com.example.healthcare.entity;

import com.example.healthcare.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The recipient of the notification.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    // The content/message of the notification.
    @Column(nullable = false)
    private String message;

    // The type/category of the notification.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // The time when the notification was created.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Whether the notification has been read.
    @Column(nullable = false)
    private boolean isRead = false;

    // Optional: Additional data as a JSON string (if needed)
    @Lob
    private String additionalData;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
