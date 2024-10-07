package com.example.quips.transaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoordinatorService {

    @Autowired
    private SistemaService sistemaService;

    @Autowired
    private RewardService rewardService;

    public void processTransition() {
        if (sistemaService.debeTransicionar()) {
            rewardService.distribuirRecompensas();
            sistemaService.transicionarFase();
        }
    }
}