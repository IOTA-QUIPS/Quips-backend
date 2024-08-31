package com.example.quips.controller;

import com.example.quips.dto.TransactionRequest;
import com.example.quips.model.Transaction;
import com.example.quips.service.CoordinatorService;
import com.example.quips.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "API para gestionar transacciones")
public class TransactionController {

    private final TransactionService transactionService;
    private final CoordinatorService coordinatorService;  // Inyección del CoordinatorService

    public TransactionController(TransactionService transactionService, CoordinatorService coordinatorService) {
        this.transactionService = transactionService;
        this.coordinatorService = coordinatorService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                request.getSenderWalletID(),
                request.getReceiverWalletID(),
                request.getAmount()
        );

        // Verificar y procesar la transición de fase después de crear la transacción
        coordinatorService.processTransition();

        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (transactionService.deleteTransaction(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
