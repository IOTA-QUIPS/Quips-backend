package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserDTO {
    private Long id;  // Agregar el campo id
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;  // Agregar campo para el número de teléfono
    private Set<String> roles;  // Si el usuario tiene roles asociadosxX
    private double coins;  // Agregar campo para las monedas

    // Constructor, Getters y Setters
    public UserDTO(Long id, String username, String firstName, String lastName, String email, String phoneNumber, Set<String> roles, double coins) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;  // Asignar el número de teléfono
        this.roles = roles;
        this.coins = coins;
    }

    // Getters y Setters
}
