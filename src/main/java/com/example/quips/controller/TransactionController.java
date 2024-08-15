package com.example.quips.controller;

import com.example.quips.dto.TransactionRequest;
import com.example.quips.model.Transaction;
import com.example.quips.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "API para gestionar transacciones")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Crear una nueva transacción
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                request.getSenderWalletID(),
                request.getReceiverWalletID(),
                request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }

    // Obtener todas las transacciones
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Obtener una transacción por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una transacción por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        boolean deleted = transactionService.deleteTransaction(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}