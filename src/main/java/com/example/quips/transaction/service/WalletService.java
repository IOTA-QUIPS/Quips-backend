package com.example.quips.transaction.service;

import com.example.quips.transaction.domain.model.Wallet;
import com.example.quips.transaction.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    // Método para añadir monedas a una wallet sin reiniciar el saldo existente
    public void addCoins(Wallet wallet, double amount) {
        // Recuperar el saldo actual de la wallet desde la base de datos
        Wallet existingWallet = walletRepository.findById(wallet.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Añadir la cantidad al saldo existente
        double newBalance = existingWallet.getCoins() + amount;
        existingWallet.setCoins(newBalance);

        // Guardar la wallet actualizada
        walletRepository.save(existingWallet);
    }

    // Método para restar monedas de una wallet asegurándose de que haya suficiente saldo
    public void subtractCoins(Wallet wallet, double amount) {
        Wallet existingWallet = walletRepository.findById(wallet.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (amount > 0 && existingWallet.getCoins() >= amount) {
            existingWallet.setCoins(existingWallet.getCoins() - amount);
            walletRepository.save(existingWallet);
        } else {
            throw new IllegalArgumentException("Saldo insuficiente o cantidad inválida");
        }
    }

    // Método para transferir monedas entre dos wallets
    public void transferCoins(Long senderWalletId, Long receiverWalletId, double amount) {
        Wallet senderWallet = walletRepository.findById(senderWalletId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet receiverWallet = walletRepository.findById(receiverWalletId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        subtractCoins(senderWallet, amount);  // Restar monedas del remitente
        addCoins(receiverWallet, amount);     // Añadir monedas al receptor
    }
}
