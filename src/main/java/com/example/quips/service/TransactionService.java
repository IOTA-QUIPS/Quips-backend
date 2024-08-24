package com.example.quips.service;

import com.example.quips.model.DAG;
import com.example.quips.model.Transaction;
import com.example.quips.model.User;
import com.example.quips.model.Wallet;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.UserRepository;
import com.example.quips.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private DAG dag;

    @Autowired
    private SistemaService sistemaService;


    @Transactional
    public Transaction createTransaction(Long senderWalletId, Long receiverWalletId, double amount) {
        int faseActual = sistemaService.getFaseActual();

        // Encontrar el usuario por walletId usando el nuevo método
        User sender = userRepository.findByWallet_Id(senderWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet ID not found"));
        User receiver = userRepository.findByWallet_Id(receiverWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet ID not found"));

        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        if (senderWallet.getCoins() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Log before transaction processing
        System.out.println("Processing transaction: Deducting " + amount + " coins from sender. Current balance: " + senderWallet.getCoins());

        String previousTransactionHash = findPreviousTransactionHash(senderWalletId);

        Transaction transaction = new Transaction(senderWalletId, receiverWalletId, amount, previousTransactionHash);
        transaction.setFase(faseActual);

        if (!dag.validateTransaction(transaction)) {
            throw new IllegalArgumentException("Transaction validation failed");
        }

        try {
            senderWallet.setCoins(senderWallet.getCoins() - amount);
            walletRepository.save(senderWallet);

            // Log after deduction
            System.out.println("After deduction: " + senderWallet.getCoins() + " coins left in sender's wallet.");

            receiverWallet.setCoins(receiverWallet.getCoins() + amount);
            walletRepository.save(receiverWallet);

            transactionRepository.save(transaction);
            dag.addTransaction(transaction);

            sistemaService.registrarTransaccion(senderWalletId, receiverWalletId, amount);

            return transaction;
        } catch (Exception e) {
            System.err.println("Error during transaction processing: " + e.getMessage());
            throw new RuntimeException("Transaction failed, rolling back.", e);
        }
    }


    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private String findPreviousTransactionHash(Long senderWalletId) {
        return transactionRepository.findTopBySenderWalletIdOrderByIdDesc(senderWalletId)
                .map(Transaction::getHash)
                .orElse("genesis_hash");
    }
}
