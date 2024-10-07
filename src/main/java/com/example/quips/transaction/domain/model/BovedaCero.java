package com.example.quips.transaction.domain.model;

public class BovedaCero {
    private long tokens;

    public BovedaCero(long cantidadInicial) {
        this.tokens = cantidadInicial;
    }

    public long getTokens() {
        return tokens;
    }

    public void restarTokens(long cantidad) {
        this.tokens -= cantidad;
    }
}
