package com.example.quips.service;

import com.example.quips.model.Wallet;
import com.example.quips.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public void addCoins(Wallet wallet, double amount) {
        wallet.setCoins(wallet.getCoins() + amount);
        walletRepository.save(wallet);
    }

    public void subtractCoins(Wallet wallet, double amount) {
        wallet.setCoins(wallet.getCoins() - amount);
        walletRepository.save(wallet);
    }

    public void transferCoins(Long senderWalletId, Long receiverWalletId, double amount) {
        Wallet senderWallet = walletRepository.findById(senderWalletId).orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet receiverWallet = walletRepository.findById(receiverWalletId).orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        subtractCoins(senderWallet, amount);
        addCoins(receiverWallet, amount);
    }
}
