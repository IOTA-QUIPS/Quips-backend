package com.example.quips.analytics.service;

import com.example.quips.authentication.domain.model.User;
import com.example.quips.transaction.service.SistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PhaseTimelineService {

    @Autowired
    private SistemaService sistemaService;

    public List<Map<String, Object>> getPhaseTimeline() {
        List<Map<String, Object>> timeline = new ArrayList<>();
        List<User> jugadores = sistemaService.getJugadores();

        for (User jugador : jugadores) {
            Map<String, Object> phaseData = new HashMap<>();
            phaseData.put("usuario", jugador.getUsername());
            phaseData.put("faseInicio", jugador.getFaseInicio());
            phaseData.put("faseActual", sistemaService.getFaseActual());
            timeline.add(phaseData);
        }
        return timeline;
    }
}