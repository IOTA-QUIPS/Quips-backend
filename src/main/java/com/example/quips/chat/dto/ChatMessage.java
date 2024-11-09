package com.example.quips.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private Long senderId;       // ID del remitente
    private Long receiverId;     // ID del receptor
    private Long conversationId; // ID de la conversación
    private String content;      // Contenido del mensaje
    private String timestamp;    // Marca de tiempo del mensaje (si es necesario)

    // Puedes agregar constructores o métodos adicionales si los necesitas
}
