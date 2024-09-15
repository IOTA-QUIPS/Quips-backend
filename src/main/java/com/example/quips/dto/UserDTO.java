package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;  // Si el usuario tiene roles asociados
    private double coins;  // Agregar campo para las monedas
    // Constructor, Getters y Setters
    public UserDTO(String username, String firstName, String lastName, String email, Set<String> roles, double coins) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.coins = coins;
    }

    // Getters y Setters
}
