package com.example.healthcare.service;

import com.example.healthcare.dto.Message.MessageDto;
import com.example.healthcare.dto.Message.SendMessageRequest;

import java.util.List;

public interface MessageService
{
    MessageDto sendMessage(Long senderId, SendMessageRequest request);
    List<MessageDto> getInbox(Long userId);
    List<MessageDto> getSent(Long userId);
    void markAsRead(Long userId, Long messageId);
}
