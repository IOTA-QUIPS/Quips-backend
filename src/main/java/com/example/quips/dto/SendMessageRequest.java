package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long conversationId;

    // Getters y setters
}