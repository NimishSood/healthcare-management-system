package com.example.healthcare.dto.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto
{
    private String content;
    private long id;
    private long senderId;
    private long receiverId;
    private LocalDateTime timestamp;
    private boolean isRead;

}
