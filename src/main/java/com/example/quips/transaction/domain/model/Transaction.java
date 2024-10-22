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

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id", referencedColumnName = "id")
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id", referencedColumnName = "id")
    private Wallet receiverWallet;

    private double amount;
    private String previousTransactionHash1;
    private String previousTransactionHash2;
    private String hash;

    // Getters y Setters, incluyendo el nuevo campo fase
    private int fase; // Nuevo campo fase

    // Campo para registrar el tiempo de la transacción
    private LocalDateTime timestamp;

    // Constructor vacío necesario para Hibernate
    public Transaction() {
    }
    // Constructor
    public Transaction(Long senderWallet, Long receiverWallet, double amount, String previousTransactionHash, String transactionHash, int faseActual) {
        this.timestamp = LocalDateTime.now(); // Se asigna la hora actual por defecto
    }



    public Transaction(Wallet senderWallet, Wallet receiverWallet, double amount, String previousTransactionHash1, String previousTransactionHash2, int fase) {
        this.senderWallet = senderWallet;
        this.receiverWallet = receiverWallet;
        this.amount = amount;
        this.previousTransactionHash1 = previousTransactionHash1;
        this.previousTransactionHash2 = previousTransactionHash2;
        this.hash = calculateHash();
        this.fase = fase;
        this.timestamp = LocalDateTime.now(); // Se asigna la hora actual por defecto
    }

    // Getters y Setters


    // Método para calcular el hash
    public String calculateHash() {
        // Preparar los datos para el hash
        String dataToHash = (senderWallet != null && senderWallet.getId() != null ? senderWallet.getId().toString() : "null")
                + (receiverWallet != null && receiverWallet.getId() != null ? receiverWallet.getId().toString() : "null")
                + amount
                + (previousTransactionHash1 != null ? previousTransactionHash1 : "")
                + (previousTransactionHash2 != null ? previousTransactionHash2 : "");

        try {
            // Crear la instancia de MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcular el hash en bytes
            byte[] hashBytes = digest.digest(dataToHash.getBytes("UTF-8"));

            // Convertir los bytes a una cadena hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();  // Devolver el hash generado
        } catch (Exception ex) {
            throw new RuntimeException("Error al calcular el hash de la transacción", ex);
        }
    }
}
