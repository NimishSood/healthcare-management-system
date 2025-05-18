package com.example.healthcare.repository;

import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderByTimestampDesc(User receiver);
    List<Message> findBySenderOrderByTimestampDesc(User sender);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :me AND m.receiver.id = :other) OR (m.sender.id = :other AND m.receiver.id = :me)")
    List<Message> findAllByParticipants(@Param("me") Long me, @Param("other") Long other);

    List<Message> findByReceiverIdOrderByTimestampDesc(Long receiverId);

    List<Message> findBySenderIdAndReceiverIdAndIsReadFalse(Long senderId, Long receiverId);


    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver ELSE m.sender END FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<User> findConversationPartners(Long userId);

    // Get latest message between two users
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.timestamp DESC")
    List<Message> findConversation(Long userId1, Long userId2);

}