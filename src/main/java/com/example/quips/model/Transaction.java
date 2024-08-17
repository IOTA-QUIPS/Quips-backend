package com.example.quips.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.security.MessageDigest;
import java.util.List;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String senderWalletId;
    private String receiverWalletId;
    private int amount;
    private String previousTransactionHash;
    private String hash;

    // Constructor vacío necesario para JPA
    public Transaction() {}

    // Constructor
    public Transaction(String senderWalletId, String receiverWalletId, int amount, String previousTransactionHash) {
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
        this.previousTransactionHash = previousTransactionHash;
        this.hash = calculateHash();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(String senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public String getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(String receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public void setPreviousTransactionHash(String previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    // Método para calcular el hash de la transacción
    public String calculateHash() {
        String dataToHash = senderWalletId + receiverWalletId + amount + previousTransactionHash;
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
