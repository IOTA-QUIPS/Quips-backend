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

    // Usando @Query para escribir una consulta SQL/JPA personalizada
    @Query("SELECT t.user, COUNT(t) as transactionCount FROM Transaction t GROUP BY t.user ORDER BY transactionCount DESC")
    List<Object[]> findTopUsersByTransactions();

    // Para obtener los usuarios que más han enviado transacciones
    @Query("SELECT t.senderWalletId, COUNT(t) as total FROM Transaction t GROUP BY t.senderWalletId ORDER BY total DESC")
    List<Object[]> findTopSenders();

    // Para obtener los usuarios que más han recibido transacciones
    @Query("SELECT t.receiverWalletId, COUNT(t) as total FROM Transaction t GROUP BY t.receiverWalletId ORDER BY total DESC")
    List<Object[]> findTopReceivers();


}
