package com.example.quips.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
    private String senderPhoneNumber;   // Cambiado a número de teléfono
    private String receiverPhoneNumber; // Cambiado a número de teléfono
    private int amount;
}
