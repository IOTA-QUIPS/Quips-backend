package com.example.quips.service;

import com.example.quips.model.DAG;
import com.example.quips.model.Transaction;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final DAG dag;
    private final SistemaService sistemaService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, DAG dag, SistemaService sistemaService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.dag = dag;
        this.sistemaService = sistemaService;

        // Inicializar el DAG con transacciones existentes
        initializeDAG();
    }

    @Transactional
    public Transaction createTransaction(Long senderWalletId, Long receiverWalletId, double amount) {
        var senderWallet = walletRepository.findById(senderWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet ID not found"));
        var receiverWallet = walletRepository.findById(receiverWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet ID not found"));

        if (senderWallet.getCoins() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        String previousTransactionHash = findPreviousTransactionHash(senderWalletId);
        Transaction transaction = new Transaction(senderWalletId, receiverWalletId, amount, previousTransactionHash);

        // Agregar la transacción al DAG antes de validarla
        dag.addTransaction(transaction);

        if (!dag.validateTransaction(transaction)) {
            throw new IllegalArgumentException("Transaction validation failed");
        }

        // Transferir coins
        senderWallet.subtractCoins(amount);
        receiverWallet.addCoins(amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        transactionRepository.save(transaction);

        sistemaService.registrarTransaccion();

        return transaction;
    }

    private String findPreviousTransactionHash(Long senderWalletId) {
        return transactionRepository.findTopBySenderWalletIdOrderByIdDesc(senderWalletId)
                .map(Transaction::getHash)
                .orElse("genesis_hash");
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

    // Inicializa el DAG con todas las transacciones existentes en la base de datos
    private void initializeDAG() {
        List<Transaction> transactions = transactionRepository.findAll();
        transactions.forEach(dag::addTransaction);
    }
}
