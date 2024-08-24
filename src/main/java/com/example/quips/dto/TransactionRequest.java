package com.example.quips.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
    private Long senderWalletID;
    private Long receiverWalletID;
    private int amount;
}
