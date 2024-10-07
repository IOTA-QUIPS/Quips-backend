package com.example.quips.transaction.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PhaseTransitionService {

    private final SistemaService sistemaService;
    private final RewardService rewardService;

    public PhaseTransitionService(SistemaService sistemaService, RewardService rewardService) {
        this.sistemaService = sistemaService;
        this.rewardService = rewardService;
    }

    @EventListener
    public void handlePhaseTransition(PhaseTransitionEvent event) {
        rewardService.distribuirRecompensas();
        sistemaService.transicionarFase();
    }
}