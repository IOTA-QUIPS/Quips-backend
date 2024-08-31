package com.example.quips.repository;

import com.example.quips.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop2ByOrderByIdDesc();

    // Métodos personalizados para contar transacciones por fase y por wallet
    int countBySenderWalletIdAndFase(Long senderWalletId, int fase);
    int countByReceiverWalletIdAndFase(Long receiverWalletId, int fase);
}
