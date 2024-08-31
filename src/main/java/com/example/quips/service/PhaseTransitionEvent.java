package com.example.quips.service;

import org.springframework.context.ApplicationEvent;

public class PhaseTransitionEvent extends ApplicationEvent {
    public PhaseTransitionEvent(Object source) {
        super(source);
    }
}