package com.example.quips.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String password;
    private String firstName;  // Nuevo campo
    private String lastName;   // Nuevo campo
    private String email;        // Nuevo campo
    private String phoneNumber;  // Nuevo campo
    private String referralCode;
}