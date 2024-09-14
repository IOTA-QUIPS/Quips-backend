package com.example.quips.service;

import com.example.quips.model.Conversation;
import com.example.quips.model.Message;
import com.example.quips.model.User;
import com.example.quips.repository.ConversationRepository;
import com.example.quips.repository.MessageRepository;
import com.example.quips.repository.UserRepository;
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

        Optional<Conversation> conversationOptional = conversationRepository.findByUser1AndUser2(user1, user2);
        if (conversationOptional.isPresent()) {
            return conversationOptional.get();
        }

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

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }
}
