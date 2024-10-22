package com.example.quips.analytics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReferralDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Long totalReferrals;

    public UserReferralDTO(Long id, String username, String firstName, String lastName, Long totalReferrals) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalReferrals = totalReferrals;
    }

    // Getters y setters...
}