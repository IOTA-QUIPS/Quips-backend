package com.example.quips.repository;

import com.example.quips.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Método para obtener la última transacción (con el ID más alto)
    Optional<Transaction> findTopByOrderByIdDesc();
}