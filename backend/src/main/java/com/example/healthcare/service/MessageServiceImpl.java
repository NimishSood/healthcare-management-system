package com.example.healthcare.service;

import com.example.healthcare.dto.Message.MessageDto;
import com.example.healthcare.dto.Message.MessageMapper;
import com.example.healthcare.dto.Message.MessageThreadDto;
import com.example.healthcare.dto.Message.SendMessageRequest;
import com.example.healthcare.dto.Profiles.UserProfileDto;
import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.UserRole;
import com.example.healthcare.exception.UnauthorizedAccessException;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.MessageRepository;
import com.example.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;


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
        return messageRepository
                .findByReceiverIdOrderByTimestampDesc(userId)
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

    @Override
    public List<MessageDto> getMessagesWithUser(Long me, Long userId) {
        // Fetch all messages where (sender=me and receiver=userId) OR (sender=userId and receiver=me)
        List<Message> messages = messageRepository
                .findAllByParticipants(me, userId); // You may need to define this in your repo
        // Sort messages by timestamp ASC
        messages.sort(Comparator.comparing(Message::getTimestamp));

        return messages.stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long me, Long userId) {
        // Find all messages where receiver = me and sender = userId and read = false
        List<Message> unread = messageRepository.findBySenderIdAndReceiverIdAndIsReadFalse(userId, me);
        for (Message msg : unread) {
            msg.setRead(true);
        }
        messageRepository.saveAll(unread);
    }

    @Override
    public List<UserProfileDto> getMessagingPartners(Long userId) {
        User me = userService.getUserById(userId);
        UserRole role = me.getRole();
        Set<User> partners = new HashSet<>();

        if (role == UserRole.PATIENT) {
            // All doctors from their appointments
            partners.addAll(appointmentRepository.findDoctorsByPatientId(userId));
            // All admins
            partners.addAll(userRepository.findByRoleAndIsDeletedFalse(UserRole.ADMIN));
        } else if (role == UserRole.DOCTOR) {
            // All patients from their appointments
            partners.addAll(appointmentRepository.findPatientsByDoctorId(userId));
            // All other doctors (except self)
            partners.addAll(
                    userRepository.findByRoleAndIsDeletedFalse(UserRole.DOCTOR).stream()
                            .filter(u -> !u.getId().equals(userId))
                            .collect(Collectors.toSet())
            );
            // All admins
            partners.addAll(userRepository.findByRoleAndIsDeletedFalse(UserRole.ADMIN));
        } else if (role == UserRole.ADMIN || role == UserRole.OWNER) {
            // Admins/owners can message anyone except themselves
            partners.addAll(userRepository.findAllExcept(userId));
        }

        // Remove self (just in case)
        partners.removeIf(u -> u.getId().equals(userId));
        // Map to DTO
        return partners.stream().map(user -> {
            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setRole(user.getRole().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    // MessageServiceImpl.java
    @Override
    public List<MessageThreadDto> getMessageThreads(Long userId) {
        List<User> partners = messageRepository.findConversationPartners(userId);

        List<MessageThreadDto> threads = new ArrayList<>();
        for (User partner : partners) {
            // Last message
            List<Message> convo = messageRepository.findConversation(userId, partner.getId());
            Message last = convo.isEmpty() ? null : convo.get(0);
            // Unread count (messages sent by partner to user, unread)
            int unread = (int) convo.stream().filter(m -> !m.isRead() && m.getReceiver().getId().equals(userId)).count();

            MessageThreadDto thread = new MessageThreadDto();
            UserProfileDto dto = new UserProfileDto();
            dto.setId(partner.getId());
            dto.setFirstName(partner.getFirstName());
            dto.setLastName(partner.getLastName());
            dto.setRole(String.valueOf(partner.getRole()));
            thread.setUser(dto);

            if (last != null) {
                thread.setLastMessage(last.getContent());
                thread.setLastMessageIsMine(last.getSender().getId().equals(userId));
                thread.setLastMessageTime(last.getTimestamp());
            }
            thread.setUnreadCount(unread);
            threads.add(thread);
        }
        // Optional: sort by latest message
        threads.sort(Comparator.comparing(MessageThreadDto::getLastMessageTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return threads;
    }






}
