package com.example.healthcare.repository;

import com.example.healthcare.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long>
{
}
