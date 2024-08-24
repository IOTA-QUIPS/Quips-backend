package com.example.quips.repository;

import com.example.quips.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTopBySenderWalletIdOrderByIdDesc(Long senderWalletId);

    boolean existsBySenderWalletIdOrReceiverWalletId(Long senderWalletId, Long receiverWalletId);

    int countBySenderWalletIdAndFase(Long senderWalletId, int fase);
    int countByReceiverWalletIdAndFase(Long receiverWalletId, int fase);
}
