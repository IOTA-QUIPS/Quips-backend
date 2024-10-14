package com.example.quips.analytics.service;

import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
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
    private TransactionRepository transactionRepository;  // Asegúrate de que esté correctamente inyectado

    public List<Map<String, Object>> getTopUsersByTransactions() {
        List<Object[]> topUsersByTransactions = transactionRepository.findTopUsersByTransactions();

        return topUsersByTransactions.stream().map(r -> {
            Map<String, Object> userTransactionData = new HashMap<>();
            Long userId = (Long) r[0];  // Suponiendo que el primer valor es el ID del usuario
            Long transactionCount = (Long) r[1];  // Suponiendo que el segundo valor es el conteo de transacciones

            // Recuperar el usuario completo usando el ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

            // Agregar la información del usuario y su cantidad de transacciones
            userTransactionData.put("user", user);
            userTransactionData.put("transactionCount", transactionCount);

            return userTransactionData;
        }).collect(Collectors.toList());
    }
}