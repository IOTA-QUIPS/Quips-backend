package com.example.quips.controller;

import com.example.quips.dto.TransactionRequest;
import com.example.quips.model.Transaction;
import com.example.quips.model.User;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.UserRepository;
import com.example.quips.service.CoordinatorService;
import com.example.quips.service.TransactionService;
import com.example.quips.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "API para gestionar transacciones")
public class TransactionController {

    private final TransactionService transactionService;
    private final CoordinatorService coordinatorService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;  // Inyectar JwtUtil para manejar la autenticación

    public TransactionController(TransactionService transactionService, CoordinatorService coordinatorService,
                                 TransactionRepository transactionRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.coordinatorService = coordinatorService;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;  // Inyectar JwtUtil
    }

    // Crear transacción con validación de token
    @CrossOrigin(origins = "*")
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestHeader("Authorization") String token,  // Obtener el token desde la cabecera
            @RequestBody TransactionRequest request) {

        // Extraer el nombre de usuario del token
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Validar que el usuario que realiza la transacción está autenticado
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Sender not authenticated"));

        // Verificar que el remitente y el receptor existen por phoneNumber
        User receiver = userRepository.findByPhoneNumber(request.getReceiverPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver phone number not found"));

        // Crear la transacción usando el servicio
        Transaction transaction = transactionService.createTransaction(
                sender.getWallet().getId(),
                receiver.getWallet().getId(),
                request.getAmount()
        );

        // Procesar la transición de fase después de la transacción
        coordinatorService.processTransition();

        return ResponseEntity.ok(transaction);
    }


    // Obtener historial de transacciones por número de teléfono
    @CrossOrigin(origins = "*")
    @GetMapping("/history/{phoneNumber}")
    public List<Transaction> getTransactionHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable String phoneNumber) {

        // Extraer el nombre de usuario del token
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Validar que el usuario autenticado puede acceder al historial
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getUsername().equals(username)) {
            throw new IllegalArgumentException("Unauthorized access to transaction history");
        }

        // Devolver el historial de transacciones del usuario
        return transactionRepository.findAllBySenderWalletIdOrReceiverWalletId(user.getWallet().getId(), user.getWallet().getId());
    }

    @CrossOrigin(origins = "*")
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
