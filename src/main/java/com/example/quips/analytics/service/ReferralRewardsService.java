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

    public List<UserReferralDTO> getTopUsersByReferrals() {
        List<Object[]> topUsersByReferrals = userRepository.findTopUsersByReferrals();

        // Convertir los resultados en una lista de DTOs
        return topUsersByReferrals.stream().map(result -> {
            User user = (User) result[0];
            Long totalReferrals = (Long) result[1];

            // Crear y devolver el DTO
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

