package com.example.quips.transaction.repository;

import com.example.quips.transaction.domain.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Método para buscar una wallet por el número de cuenta del usuario
    Optional<Wallet> findByUserAccountNumber(String accountNumber);
}
