package com.example.quips.shared.util;

import java.util.Random;

public class CodeGenerator {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateReferralCode() {
        Random random = new Random();
        StringBuilder referralCode = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            referralCode.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return referralCode.toString();
    }
}

