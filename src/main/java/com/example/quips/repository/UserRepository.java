package com.example.quips.repository;

import com.example.quips.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Método para buscar un usuario por su walletId
    Optional<User> findByWalletId(String walletId);
}