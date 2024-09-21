package com.example.quips.repository;

import com.example.quips.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber); // Nuevo método
    List<User> findByPhoneNumberIn(List<String> phoneNumbers); // Método para buscar usuarios por números de teléfono
}
