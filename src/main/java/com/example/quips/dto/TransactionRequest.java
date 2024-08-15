package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
    // Getters y Setters
    private String senderWalletID;
    private String receiverWalletID;
    private int amount;

}