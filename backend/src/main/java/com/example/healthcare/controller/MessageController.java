package com.example.healthcare.controller;
import com.example.healthcare.dto.Message.MessageDto;
import com.example.healthcare.dto.Message.MessageThreadDto;
import com.example.healthcare.dto.Message.SendMessageRequest;
import com.example.healthcare.dto.Profiles.UserProfileDto;
import com.example.healthcare.entity.User;
import com.example.healthcare.service.MessageService;
import com.example.healthcare.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    /**
     * Send a message to another user (Doctor or Patient).
     */
    @GetMapping("/whoami")
    public String whoami() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "NOT AUTHENTICATED";
        }
        return "Authenticated as: " + auth.getName() + ", roles: " + auth.getAuthorities();
    }

    @PostMapping("/send")
    public MessageDto sendMessage(@RequestBody @Validated SendMessageRequest req) {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.sendMessage(me, req);
    }

    /**
     * List all received messages (inbox).
     */
    @GetMapping("/inbox")
    public List<MessageDto> getInbox() {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.getInbox(me);
    }

    /**
     * List all sent messages.
     */
    @GetMapping("/sent")
    public List<MessageDto> getSent() {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.getSent(me);
    }

    /**
     * Mark a received message as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Long me = securityUtils.getAuthenticatedUserId();
        messageService.markAsRead(me, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/with/{userId}")
    public List<MessageDto> getMessagesWithUser(@PathVariable Long userId) {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.getMessagesWithUser(me, userId);
    }

    @PutMapping("/with/{userId}/read")
    public ResponseEntity<Void> markConversationAsRead(@PathVariable Long userId) {
        Long me = securityUtils.getAuthenticatedUserId();
        messageService.markConversationAsRead(me, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/partners")
    public List<UserProfileDto> getMessagingPartners() {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.getMessagingPartners(me);
    }

    @GetMapping("/threads")
    public List<MessageThreadDto> getMessageThreads() {
        Long me = securityUtils.getAuthenticatedUserId();
        return messageService.getMessageThreads(me);
    }









}