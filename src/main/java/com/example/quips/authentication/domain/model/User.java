package com.example.quips.authentication.domain.model;

import com.example.quips.transaction.domain.model.Wallet;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    private String sixDigitPin; // Clave de 6 dígitos opcional


    // Nuevos campos para nombre y apellido

    private String firstName;

    private String lastName;

    // Getters y Setters para los nuevos campos

    private String email;

    private String phoneNumber;

    private String referralCode;  // Campo para el código de referido

    // Código de referido usado al registrarse
    private String referralCodeUsed;


    @Column(unique = true, nullable = false)
    private String accountNumber;

    // Campo para la activación del usuario
    private boolean active = false;  // Por defecto, el usuario no estará activo hasta que verifique su correo.



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    @JsonManagedReference
    private Wallet wallet;




    // Relación con roles
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Campo para registrar la fase en la que el usuario inició
    private int faseInicio;

    // Campo para el total de referidos
    private int totalReferrals = 0;


    // Getters y Setters

    // Método para incrementar el total de referidos
    public void incrementTotalReferrals() {
        this.totalReferrals++;
    }

    public User() {
        this.faseInicio = 1;  // Por defecto, todos los usuarios empiezan en la fase 1
        this.accountNumber = UUID.randomUUID().toString();  // Generar un número de cuenta único al crear el usuario
    }


}