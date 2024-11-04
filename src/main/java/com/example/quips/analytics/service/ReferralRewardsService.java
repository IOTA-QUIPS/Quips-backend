package com.example.quips.analytics.service;

import com.example.quips.analytics.dto.UserReferralDTO;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class ReferralRewardsService {

    @Autowired
    private UserRepository userRepository;

    // Método para obtener los referidos de un usuario específico
    public List<UserReferralDTO> getReferralsByUserId(Long userId) {
        // Suponiendo que tienes un método en el repositorio para obtener los referidos de un usuario
        List<User> referrals = userRepository.findReferralsByUserId(userId);

        // Convertir los referidos en una lista de DTOs
        return referrals.stream().map(referral -> new UserReferralDTO(
                referral.getId(),
                referral.getUsername(),
                referral.getFirstName(),
                referral.getLastName(),
                1L  // Asumimos que cada referido cuenta como 1
        )).collect(Collectors.toList());
    }

    public List<UserReferralDTO> getTopUsersByReferrals() {
        List<Object[]> topUsersByReferrals = userRepository.findTopUsersByReferrals();

        // Convertir los resultados en una lista de DTOs
        return topUsersByReferrals.stream().map(result -> {
            User user = (User) result[0];
            Long totalReferrals = (Long) result[1];

            return new UserReferralDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    totalReferrals
            );
        }).collect(Collectors.toList());
    }
}

