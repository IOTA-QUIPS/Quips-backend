package com.example.quips.controller;

import com.example.quips.model.Conversation;
import com.example.quips.model.Message;
import com.example.quips.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Crear o obtener una conversación
    @PostMapping("/conversation")
    public ResponseEntity<Conversation> createOrGetConversation(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Conversation conversation = chatService.getOrCreateConversation(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }

    // Enviar un mensaje
    @PostMapping("/sendMessage")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam Long conversationId) {
        Message message = chatService.sendMessage(senderId, receiverId, content, conversationId);
        return ResponseEntity.ok(message);
    }

    // Obtener todos los mensajes de una conversación
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<List<Message>> getConversationMessages(@PathVariable Long conversationId) {
        List<Message> messages = chatService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
}
