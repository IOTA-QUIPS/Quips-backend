package com.example.quips.controller;

import com.example.quips.dto.SendMessageRequest;
import com.example.quips.model.Conversation;
import com.example.quips.model.Message;
import com.example.quips.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Crear o obtener una conversación
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @PostMapping("/conversation")
    public ResponseEntity<Conversation> createOrGetConversation(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Conversation conversation = chatService.getOrCreateConversation(user1Id, user2Id);

        // Manejar el caso en el que aún no haya mensajes
        if (conversation.getMessages() == null) {
            conversation.setMessages(new ArrayList<>());  // Establecer una lista vacía si no hay mensajes
        }

        return ResponseEntity.ok(conversation);
    }

    // Enviar un mensaje
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @PostMapping("/sendMessage")
    public ResponseEntity<Message> sendMessage(@RequestBody SendMessageRequest request) {  // Cambia a @RequestBody
        Message message = chatService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getContent(), request.getConversationId());
        return ResponseEntity.ok(message);
    }

    // Obtener todos los mensajes de una conversación
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<List<Message>> getConversationMessages(@PathVariable Long conversationId) {
        List<Message> messages = chatService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
}
