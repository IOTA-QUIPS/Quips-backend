package com.example.quips.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.security.MessageDigest;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long senderWalletId;
    private Long receiverWalletId;
    private double amount;
    private String previousTransactionHash1;
    private String previousTransactionHash2;
    private String hash;

    private int fase; // Nuevo campo fase

    public Transaction() {}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(Long senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public Long getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(Long receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPreviousTransactionHash1() {
        return previousTransactionHash1;
    }

    public void setPreviousTransactionHash1(String previousTransactionHash1) {
        this.previousTransactionHash1 = previousTransactionHash1;
    }

    public String getPreviousTransactionHash2() {
        return previousTransactionHash2;
    }

    public void setPreviousTransactionHash2(String previousTransactionHash2) {
        this.previousTransactionHash2 = previousTransactionHash2;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    // Getters y Setters, incluyendo el nuevo campo fase
    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }

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
