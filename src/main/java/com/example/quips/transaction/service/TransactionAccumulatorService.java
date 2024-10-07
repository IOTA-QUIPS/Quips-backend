package com.example.quips.transaction.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TransactionAccumulatorService {

    private final SistemaService sistemaService;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionAccumulatorService(SistemaService sistemaService, ApplicationEventPublisher eventPublisher) {
        this.sistemaService = sistemaService;
        this.eventPublisher = eventPublisher;
    }

    public void accumulateTransaction(Long senderWalletId, Long receiverWalletId, double amount) {
        sistemaService.registrarTransaccion();

        if (sistemaService.debeTransicionar()) {
            eventPublisher.publishEvent(new PhaseTransitionEvent(this));
        }
    }
}