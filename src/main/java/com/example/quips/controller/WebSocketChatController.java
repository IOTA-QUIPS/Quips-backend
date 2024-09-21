package com.example.quips.controller;

import com.example.quips.dto.ChatMessage;
import com.example.quips.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage handleMessage(ChatMessage chatMessage) {
        // Aquí podrías guardar el mensaje y luego devolverlo a todos los usuarios conectados
        return chatMessage;
    }
}
