package com.example.quips.analytics.service;

import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.transaction.domain.model.BovedaCero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenAnalyticsService {

    @Autowired
    private BovedaCero bovedaCero;
    @Autowired
    private SistemaConfig sistemaConfig;

    public Map<String, Object> getTokenStatus() {
        long tokensEmitidos = sistemaConfig.getTokensIniciales();
        long tokensRestantes = bovedaCero.getTokens();
        long tokensEnCirculacion = tokensEmitidos - tokensRestantes;

        Map<String, Object> tokenStatus = new HashMap<>();
        tokenStatus.put("tokensEmitidos", tokensEmitidos);
        tokenStatus.put("tokensEnCirculacion", tokensEnCirculacion);
        tokenStatus.put("tokensRestantes", tokensRestantes);
        return tokenStatus;
    }
}
