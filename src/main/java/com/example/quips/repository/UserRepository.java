package com.example.quips.repository;

import com.example.quips.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Buscar usuario por el ID de la wallet
    Optional<User> findByWallet_Id(Long walletId);
}