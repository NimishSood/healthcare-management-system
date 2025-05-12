package com.example.healthcare.service;

import com.example.healthcare.dto.Message.MessageDto;
import com.example.healthcare.dto.Message.MessageMapper;
import com.example.healthcare.dto.Message.SendMessageRequest;
import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Override
    @Transactional
    public MessageDto sendMessage(Long senderId, SendMessageRequest req) {
        User sender   = userService.getUserById(senderId);
        User receiver = userService.getUserById(req.getReceiverId());

        if (sender.getRole() == receiver.getRole()) {
            throw new UnauthorizedAccessException("Cannot message users with same role.");
        }

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(req.getContent());
        messageRepository.save(msg);

        return MessageMapper.toDto(msg);
    }

    @Override
    public List<MessageDto> getInbox(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository
                .findByReceiverOrderByTimestampDesc(user)
                .stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> getSent(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository
                .findBySenderOrderByTimestampDesc(user)
                .stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!msg.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Only the receiver can mark as read.");
        }
        msg.setRead(true);
        messageRepository.save(msg);
    }
}
