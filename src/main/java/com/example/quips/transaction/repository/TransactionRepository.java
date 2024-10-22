package com.example.quips.transaction.repository;

import com.example.quips.transaction.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTop2ByOrderByIdDesc();

    List<Transaction> findAllBySenderWalletIdOrReceiverWalletId(Long senderWalletId, Long receiverWalletId);
    // Métodos personalizados para contar transacciones por fase y por wallet
    int countBySenderWalletIdAndFase(Long senderWalletId, int fase);
    int countByReceiverWalletIdAndFase(Long receiverWalletId, int fase);

    // Usando @Query para contar las transacciones por usuario a través de sus wallets
    @Query("SELECT w.user, COUNT(t) as transactionCount " +
            "FROM Transaction t " +
            "JOIN t.senderWallet w " +
            "GROUP BY w.user " +
            "ORDER BY transactionCount DESC")
    List<Object[]> findTopUsersByTransactions();

    // Para obtener los IDs de las wallets de los remitentes y el conteo de transacciones
    @Query("SELECT t.senderWallet.id, COUNT(t) as total FROM Transaction t GROUP BY t.senderWallet.id ORDER BY total DESC")
    List<Object[]> findTopSenders();

    // Para obtener los IDs de las wallets de los destinatarios y el conteo de transacciones
    @Query("SELECT t.receiverWallet.id, COUNT(t) as total FROM Transaction t GROUP BY t.receiverWallet.id ORDER BY total DESC")
    List<Object[]> findTopReceivers();


}
