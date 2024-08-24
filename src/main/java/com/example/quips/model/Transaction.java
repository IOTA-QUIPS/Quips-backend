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
    private double amount;  // Cambiado a double
    private String previousTransactionHash;
    private String hash;
    private int fase; // Agregar esta propiedad para almacenar la fase

    // Constructor vacío necesario para JPA
    public Transaction() {}

    // Constructor
    public Transaction(Long senderWalletId, Long receiverWalletId, double amount, String previousTransactionHash) {
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

    public String getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public void setPreviousTransactionHash(String previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    // Getters y setters para la nueva propiedad
    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
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
