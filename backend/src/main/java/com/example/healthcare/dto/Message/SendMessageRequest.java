package com.example.healthcare.dto.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest
{
    @NotNull
    private Long receiverId;

    @NotBlank
    private String content;

}
