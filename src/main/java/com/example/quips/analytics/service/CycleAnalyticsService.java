package com.example.quips.analytics.service;

import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.transaction.service.SistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CycleAnalyticsService {

    @Autowired
    private SistemaService sistemaService;
    @Autowired
    private SistemaConfig sistemaConfig;

    public Map<String, Object> getCycleStatus() {
        int faseActual = sistemaService.getFaseActual();
        int jugadoresEnFase = sistemaService.getJugadoresEnFase();
        int cuotaFaseActual = sistemaConfig.getCuotasPorFase()[faseActual - 1];

        double porcentajeCompletado = ((double) jugadoresEnFase / cuotaFaseActual) * 100;

        Map<String, Object> status = new HashMap<>();
        status.put("faseActual", faseActual);
        status.put("jugadoresEnFase", jugadoresEnFase);
        status.put("cuotaFaseActual", cuotaFaseActual);
        status.put("porcentajeCompletado", porcentajeCompletado);
        return status;
    }
}
