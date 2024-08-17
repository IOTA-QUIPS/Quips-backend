package com.example.quips.service;

import com.example.quips.model.BovedaCero;
import com.example.quips.model.DAG;
import com.example.quips.model.Transaction;
import com.example.quips.model.User;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.UserRepository;
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
    private BovedaCero bovedaCero; // Instancia de Bóveda Cero

    @Autowired
    private DAG dag; // Instancia de DAG

    @Autowired
    private Sistema sistema; // Inyección del sistema para manejar fases

    public Transaction createTransaction(String senderWalletId, String receiverWalletId, double amount) {
        User sender = userRepository.findByWalletId(senderWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet ID not found"));

        if (sender.getCoins() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        String previousTransactionHash = findPreviousTransactionHash(senderWalletId, receiverWalletId);

        Transaction transaction = new Transaction(senderWalletId, receiverWalletId, amount, previousTransactionHash);

        if (dag.validateTransaction(transaction)) {
            sender.setCoins(sender.getCoins() - amount);
            User receiver = userRepository.findByWalletId(receiverWalletId)
                    .orElseThrow(() -> new IllegalArgumentException("Receiver wallet ID not found"));
            receiver.setCoins(receiver.getCoins() + amount);

            transactionRepository.save(transaction);
            userRepository.save(sender);
            userRepository.save(receiver);

            dag.addTransaction(transaction); // Añadir la transacción al DAG

            // Registrar la transacción en el sistema para verificar la fase
            sistema.registrarTransaccion();

            return transaction;
        } else {
            throw new IllegalArgumentException("Transaction validation failed");
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

    private String findPreviousTransactionHash(String senderWalletId, String receiverWalletId) {
        // Buscar la transacción más reciente del remitente y devolver su hash
        return transactionRepository.findTopBySenderWalletIdOrderByIdDesc(senderWalletId)
                .map(Transaction::getHash)
                .orElse("genesis_hash"); // Usar "genesis_hash" solo si no se encuentra ninguna transacción previa
    }
}
