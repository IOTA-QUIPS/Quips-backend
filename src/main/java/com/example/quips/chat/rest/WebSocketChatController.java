package com.example.quips.chat.rest;

import com.example.quips.chat.dto.ChatMessage;
import com.example.quips.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{senderId}/{receiverId}")
    public void handleMessage(@DestinationVariable Long senderId,
                              @DestinationVariable Long receiverId,
                              ChatMessage chatMessage) {
        // Guarda el mensaje en la base de datos si es necesario
        chatService.sendMessage(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                chatMessage.getContent(),
                chatMessage.getConversationId()
        );

        // Enviar el mensaje solo a la conversación específica
        messagingTemplate.convertAndSend(
                "/topic/messages/" + senderId + "/" + receiverId,
                chatMessage
        );
    }
}
