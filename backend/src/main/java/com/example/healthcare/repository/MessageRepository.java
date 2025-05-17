package com.example.healthcare.repository;

import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderByTimestampDesc(User receiver);
    List<Message> findBySenderOrderByTimestampDesc(User sender);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :me AND m.receiver.id = :other) OR (m.sender.id = :other AND m.receiver.id = :me)")
    List<Message> findAllByParticipants(@Param("me") Long me, @Param("other") Long other);
}