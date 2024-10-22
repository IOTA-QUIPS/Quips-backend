package com.example.quips.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserTransactionDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String accountNumber;
    private int totalTransactions;

    // Constructor
    public UserTransactionDTO(Long id, String username, String firstName, String lastName, String email, String accountNumber, int totalTransactions) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accountNumber = accountNumber;
        this.totalTransactions = totalTransactions;
    }

}