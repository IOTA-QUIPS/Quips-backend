package com.example.quips.transaction.domain.model;

import com.example.quips.authentication.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.MessageDigest;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long senderWalletId;
    private Long receiverWalletId;
    private double amount;
    private String previousTransactionHash1;
    private String previousTransactionHash2;
    private String hash;

    // Getters y Setters, incluyendo el nuevo campo fase
    private int fase; // Nuevo campo fase

    // Campo para registrar el tiempo de la transacción
    private LocalDateTime timestamp;

    // Constructor
    public Transaction() {
        this.timestamp = LocalDateTime.now(); // Se asigna la hora actual por defecto
    }



    public Transaction(Long senderWalletId, Long receiverWalletId, double amount, String previousTransactionHash1, String previousTransactionHash2, int fase) {
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
        this.previousTransactionHash1 = previousTransactionHash1;
        this.previousTransactionHash2 = previousTransactionHash2;
        this.hash = calculateHash();
        this.fase = fase;
    }

    // Getters y Setters


    public String calculateHash() {
        String dataToHash = senderWalletId + receiverWalletId + amount + previousTransactionHash1 + previousTransactionHash2;
        MessageDigest digest;
        String encoded = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dataToHash.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            encoded = hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encoded;
    }
}
