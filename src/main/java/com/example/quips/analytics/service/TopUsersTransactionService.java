package com.example.quips.analytics.service;

import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
import com.example.quips.transaction.domain.model.Transaction;
import com.example.quips.transaction.dto.UserTransactionDTO;
import com.example.quips.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopUsersTransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    // Obtener los principales usuarios con mayor número de transacciones (enviadas o recibidas)
    public List<UserTransactionDTO> getTopUsersByTransactions() {
        List<Object[]> topUsers = transactionRepository.findTopUsersByTransactions();

        return topUsers.stream().map(r -> {
            User user = (User) r[0];
            Long totalTransactions = (Long) r[1];

            return new UserTransactionDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getAccountNumber(),
                    totalTransactions.intValue()  // Total de transacciones (enviadas + recibidas)
            );
        }).collect(Collectors.toList());
    }


// Obtener los principales remitentes (usuarios que más han enviado transacciones)

    public List<UserTransactionDTO> getTopSenders() {
        List<Object[]> topSenders = transactionRepository.findTopSenders();

        return topSenders.stream().map(r -> {
            Long senderWalletId = (Long) r[0];
            Long totalTransactions = (Long) r[1];

            User sender = userRepository.findByWalletId(senderWalletId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para wallet ID: " + senderWalletId));

            return new UserTransactionDTO(
                    sender.getId(),
                    sender.getUsername(),
                    sender.getFirstName(),
                    sender.getLastName(),
                    sender.getEmail(),
                    sender.getAccountNumber(),
                    totalTransactions.intValue()  // Total de transacciones realizadas
            );
        }).collect(Collectors.toList());
    }

    // Obtener los principales destinatarios (usuarios que más han recibido transacciones)
    public List<UserTransactionDTO> getTopReceivers() {
        List<Object[]> topReceivers = transactionRepository.findTopReceivers();

        return topReceivers.stream().map(r -> {
            Long receiverWalletId = (Long) r[0];
            Long totalTransactions = (Long) r[1];

            User receiver = userRepository.findByWalletId(receiverWalletId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para wallet ID: " + receiverWalletId));

            return new UserTransactionDTO(
                    receiver.getId(),
                    receiver.getUsername(),
                    receiver.getFirstName(),
                    receiver.getLastName(),
                    receiver.getEmail(),
                    receiver.getAccountNumber(),
                    totalTransactions.intValue()  // Total de transacciones recibidas
            );
        }).collect(Collectors.toList());
    }

    // Obtener las transacciones enviadas por un usuario específico
    public List<UserTransactionDTO> getSentTransactionsByUserId(Long userId) {
        List<Transaction> sentTransactions = transactionRepository.findSentTransactionsByUserId(userId);

        return sentTransactions.stream().map(transaction -> {
            User sender = transaction.getSenderWallet().getUser();
            return new UserTransactionDTO(
                    sender.getId(),
                    sender.getUsername(),
                    sender.getFirstName(),
                    sender.getLastName(),
                    sender.getEmail(),
                    sender.getAccountNumber(),
                    1  // Representa una sola transacción en el contexto de este DTO
            );
        }).collect(Collectors.toList());
    }

    // Obtener las transacciones recibidas por un usuario específico
    public List<UserTransactionDTO> getReceivedTransactionsByUserId(Long userId) {
        List<Transaction> receivedTransactions = transactionRepository.findReceivedTransactionsByUserId(userId);

        return receivedTransactions.stream().map(transaction -> {
            User receiver = transaction.getReceiverWallet().getUser();
            return new UserTransactionDTO(
                    receiver.getId(),
                    receiver.getUsername(),
                    receiver.getFirstName(),
                    receiver.getLastName(),
                    receiver.getEmail(),
                    receiver.getAccountNumber(),
                    1  // Representa una sola transacción en el contexto de este DTO
            );
        }).collect(Collectors.toList());
    }
}