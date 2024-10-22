package com.example.quips.analytics.service;

import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
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

    // Obtener los usuarios con mayor cantidad de transacciones
    public List<Map<String, Object>> getTopUsersByTransactions() {
        List<Object[]> topUsersByTransactions = transactionRepository.findTopUsersByTransactions();

        return topUsersByTransactions.stream().map(r -> {
            Map<String, Object> userTransactionData = new HashMap<>();
            User user = (User) r[0];  // El primer valor es el usuario
            Long transactionCount = (Long) r[1];  // El segundo valor es el conteo de transacciones

            // Agregar la información del usuario y su cantidad de transacciones
            userTransactionData.put("user", user);
            userTransactionData.put("transactionCount", transactionCount);

            return userTransactionData;
        }).collect(Collectors.toList());
    }

    // Obtener los usuarios con mayor cantidad de transacciones
    public List<UserTransactionDTO> getTopSenders() {
        List<Object[]> topSenders = transactionRepository.findTopSenders();

        return topSenders.stream().map(r -> {
            Long senderWalletId = (Long) r[0];
            Long totalTransactions = (Long) r[1];

            // Recuperar el usuario usando el ID de la wallet
            User sender = userRepository.findByWalletId(senderWalletId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para wallet ID: " + senderWalletId));

            // Crear el DTO con la información necesaria
            return new UserTransactionDTO(sender.getId(), sender.getUsername(), sender.getFirstName(),
                    sender.getLastName(), sender.getEmail(), sender.getAccountNumber(), totalTransactions.intValue());
        }).collect(Collectors.toList());
    }

    public List<UserTransactionDTO> getTopReceivers() {
        List<Object[]> topReceivers = transactionRepository.findTopReceivers();

        return topReceivers.stream().map(r -> {
            Long receiverWalletId = (Long) r[0];
            Long totalTransactions = (Long) r[1];

            User receiver = userRepository.findByWalletId(receiverWalletId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para wallet ID: " + receiverWalletId));

            return new UserTransactionDTO(receiver.getId(), receiver.getUsername(), receiver.getFirstName(),
                    receiver.getLastName(), receiver.getEmail(), receiver.getAccountNumber(), totalTransactions.intValue());
        }).collect(Collectors.toList());
    }
}
