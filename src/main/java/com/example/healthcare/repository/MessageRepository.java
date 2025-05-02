package com.example.healthcare.repository;

import com.example.healthcare.entity.Message;
import com.example.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderByTimestampDesc(User receiver);
    List<Message> findBySenderOrderByTimestampDesc(User sender);
}