package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private int amount;
}
