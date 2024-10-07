package com.example.quips.authentication.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserDTO {
    private Long id;  // Agregar el campo id
    private String username;
    private String sixDigitPin;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String accountNumber;
    private String referralCode;// Agregar campo para el número de teléfono
    private Set<String> roles;  // Si el usuario tiene roles asociadosxX
    private double coins;  // Agregar campo para las monedas
    private boolean active;

    // Constructor, Getters y Setters
    public UserDTO(Long id, String username, String sixDigitPin, String firstName, String lastName, String email, String phoneNumber, String accountNumber, String referralCode , Set<String> roles, double coins, boolean active) {
        this.id = id;
        this.username = username;
        this.sixDigitPin = sixDigitPin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;  // Asignar el número de teléfono
        this.accountNumber = accountNumber;
        this.referralCode = referralCode;
        this.roles = roles;
        this.coins = coins;
        this.active = active;  // Inicializar el campo active
    }

    // Getters y Setters
}
