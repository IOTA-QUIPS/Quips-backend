package com.example.quips.analytics.service;

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

    public List<Map<String, Object>> getTopUsersByReferrals() {
        List<Object[]> topUsersByReferrals = userRepository.findTopUsersByReferrals();

        return topUsersByReferrals.stream().map(r -> {
            Map<String, Object> userReferralData = new HashMap<>();
            userReferralData.put("user", (User) r[0]);
            userReferralData.put("totalReferrals", (Long) r[1]);  // Suponiendo que el total de referidos es de tipo Long
            return userReferralData;
        }).collect(Collectors.toList());
    }
}

