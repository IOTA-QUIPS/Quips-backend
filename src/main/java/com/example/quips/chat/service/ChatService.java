package com.example.quips.chat.service;

import com.example.quips.chat.domain.model.Conversation;
import com.example.quips.chat.domain.model.Message;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.chat.repository.ConversationRepository;
import com.example.quips.chat.repository.MessageRepository;
import com.example.quips.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        // Agrega un log para verificar el ID de los usuarios
        System.out.println("Buscando conversación entre: " + user1.getId() + " y " + user2.getId());

        Optional<Conversation> conversationOptional = conversationRepository.findByUser1AndUser2OrUser1AndUser2(user1, user2, user2, user1);

        if (conversationOptional.isPresent()) {
            System.out.println("Conversación encontrada con ID: " + conversationOptional.get().getId());
            return conversationOptional.get();
        } else {
            System.out.println("No se encontró conversación. Creando una nueva.");
        }

        // Si no existe la conversación, crear una nueva
        Conversation conversation = new Conversation();
        conversation.setUser1(user1);
        conversation.setUser2(user2);
        return conversationRepository.save(conversation);
    }


    public Message sendMessage(Long senderId, Long receiverId, String content, Long conversationId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Conversation conversation;

        // Si el conversationId es null, intenta obtener o crear una conversación
        if (conversationId == null) {
            conversation = getOrCreateConversation(senderId, receiverId);
        } else {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setConversation(conversation);  // Asegurarse de que se establece la conversación correcta
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }
}
