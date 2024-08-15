package com.example.quips.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")  // Cambia el nombre de la tabla a "users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String walletId;
    private int coins;

    // Constructor vacío
    public User() {}

    // Constructor completo
    public User(String username, String password, String walletId, int coins) {
        this.username = username;
        this.password = password;
        this.walletId = walletId;
        this.coins = coins;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
