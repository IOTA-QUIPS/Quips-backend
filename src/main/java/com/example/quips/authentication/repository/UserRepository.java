package com.example.quips.authentication.repository;

import com.example.quips.authentication.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // Consulta para encontrar los usuarios con más referidos
    @Query("SELECT u, COUNT(r.id) as totalReferrals FROM User u LEFT JOIN User r ON u.referralCode = r.referralCode GROUP BY u.id ORDER BY totalReferrals DESC")
    List<Object[]> findTopUsersByReferrals();

    Optional<User> findByPhoneNumber(String phoneNumber); // Buscar por número de teléfono

    List<User> findByPhoneNumberIn(List<String> phoneNumbers); // Buscar usuarios por lista de números de teléfono

    // Buscar usuario por su código de referido (referralCode)
    Optional<User> findByReferralCode(String referralCode);

    // Buscar usuario por email
    Optional<User> findByEmail(String email); // Nuevo método para verificar unicidad del email
}
