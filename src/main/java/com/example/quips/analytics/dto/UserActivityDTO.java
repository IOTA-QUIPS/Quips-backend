package com.example.quips.analytics.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserActivityDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private int referidos;
    private int transaccionesEnviadas;
    private int transaccionesRecibidas;
    private String nivelActividad;

    public UserActivityDTO(Long userId, String username, String firstName, String lastName, int referidos, int transaccionesEnviadas, int transaccionesRecibidas, String nivelActividad) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.referidos = referidos;
        this.transaccionesEnviadas = transaccionesEnviadas;
        this.transaccionesRecibidas = transaccionesRecibidas;
        this.nivelActividad = nivelActividad;
    }

    // Getters y setters generados por Lombok
}
