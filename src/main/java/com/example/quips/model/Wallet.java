package com.example.quips.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double coins;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL)
    @JsonBackReference
    private User user;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Método para añadir monedas
    public void addCoins(double amount) {
        if (amount > 0) {
            this.coins += amount;
        }
    }

    // Método para restar monedas
    public void subtractCoins(double amount) {
        if (amount > 0 && this.coins >= amount) {
            this.coins -= amount;
        } else {
            throw new IllegalArgumentException("Saldo insuficiente o cantidad inválida");
        }
    }
}