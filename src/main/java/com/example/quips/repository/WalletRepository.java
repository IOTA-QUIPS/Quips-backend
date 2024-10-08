package com.example.quips.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.quips.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
