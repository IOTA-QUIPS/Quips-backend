package com.example.quips.repository;

import com.example.quips.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByWallet_Id(Long walletId);

    // Método para buscar usuario por nombre de usuario (username)
    Optional<User> findByUsername(String username);
}
