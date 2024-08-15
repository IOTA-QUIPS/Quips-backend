package com.example.quips.service;

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

    // Método para crear una nueva transacción
    public Transaction createTransaction(String senderWalletId, String receiverWalletId, int amount) {
        // Verificar el saldo del usuario remitente
        User sender = userRepository.findByWalletId(senderWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet ID not found"));

        if (sender.getCoins() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Selección de transacciones anteriores para el tangle
        String previousTransactionHash = findPreviousTransactionHash(senderWalletId, receiverWalletId);

        // Crear la nueva transacción
        Transaction transaction = new Transaction();
        transaction.setSenderWalletId(senderWalletId);
        transaction.setReceiverWalletId(receiverWalletId);
        transaction.setAmount(amount);
        transaction.setPreviousTransactionHash(previousTransactionHash);

        // Actualizar el saldo de los usuarios
        sender.setCoins(sender.getCoins() - amount);
        User receiver = userRepository.findByWalletId(receiverWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet ID not found"));
        receiver.setCoins(receiver.getCoins() + amount);

        // Guardar la transacción y actualizar los usuarios
        transactionRepository.save(transaction);
        userRepository.save(sender);
        userRepository.save(receiver);

        return transaction;
    }

    // Método para obtener una transacción por su ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Método para obtener todas las transacciones
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Método para eliminar una transacción por su ID
    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Método auxiliar para encontrar el hash de la transacción anterior
    private String findPreviousTransactionHash(String senderWalletId, String receiverWalletId) {
        // Lógica para seleccionar el hash de la transacción anterior
        return transactionRepository.findTopByOrderByIdDesc()
                .map(Transaction::getPreviousTransactionHash)
                .orElse("genesis_hash");
    }
}
