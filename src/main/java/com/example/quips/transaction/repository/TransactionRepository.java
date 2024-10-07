package com.example.quips.transaction.repository;

import com.example.quips.transaction.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop2ByOrderByIdDesc();

    List<Transaction> findAllBySenderWalletIdOrReceiverWalletId(Long senderWalletId, Long receiverWalletId);
    // Métodos personalizados para contar transacciones por fase y por wallet
    int countBySenderWalletIdAndFase(Long senderWalletId, int fase);
    int countByReceiverWalletIdAndFase(Long receiverWalletId, int fase);
}
