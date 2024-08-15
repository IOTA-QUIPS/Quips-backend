package com.example.quips.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Transaction {
    // Getters y Setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String senderWalletId;
    private String receiverWalletId;
    private int amount;
    private String previousTransactionHash;

    // Constructor vacío
    public Transaction() {}

    // Constructor completo
    public Transaction(String senderWalletId, String receiverWalletId, int amount, String previousTransactionHash) {
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
        this.previousTransactionHash = previousTransactionHash;
    }

}
