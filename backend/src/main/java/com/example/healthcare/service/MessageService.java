package com.example.healthcare.service;

import com.example.healthcare.dto.Message.MessageDto;
import com.example.healthcare.dto.Message.MessageThreadDto;
import com.example.healthcare.dto.Message.SendMessageRequest;
import com.example.healthcare.dto.Profiles.UserProfileDto;

import java.util.List;

public interface MessageService
{
    MessageDto sendMessage(Long senderId, SendMessageRequest request);
    List<MessageDto> getInbox(Long userId);
    List<MessageDto> getSent(Long userId);
    void markAsRead(Long userId, Long messageId);

    List<MessageDto> getMessagesWithUser(Long me, Long userId);

    void markConversationAsRead(Long me, Long userId);

    List<UserProfileDto> getMessagingPartners(Long me);

    List<MessageThreadDto> getMessageThreads(Long userId);
}
