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

    @Query("SELECT u, COUNT(r) as totalReferrals " +
            "FROM User u LEFT JOIN User r ON r.referralCodeUsed = u.referralCode " +
            "GROUP BY u.id ORDER BY totalReferrals DESC")
    List<Object[]> findTopUsersByReferrals();

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByPhoneNumberIn(List<String> phoneNumbers);

    Optional<User> findByReferralCode(String referralCode);

    Optional<User> findByWalletId(Long walletId);

    Optional<User> findByEmail(String email);

    // Nuevo método para obtener los usuarios referidos por un usuario específico
    @Query("SELECT u FROM User u WHERE u.referralCodeUsed = (SELECT user.referralCode FROM User user WHERE user.id = :userId)")
    List<User> findReferralsByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
