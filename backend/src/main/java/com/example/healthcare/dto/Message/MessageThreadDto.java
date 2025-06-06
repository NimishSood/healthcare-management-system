package com.example.healthcare.dto.Message;

import com.example.healthcare.dto.Profiles.UserProfileDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageThreadDto {
    private UserProfileDto user;    // Conversation partner
    private String lastMessage;
    private boolean lastMessageIsMine;
    private int unreadCount;
    private LocalDateTime lastMessageTime;
}

