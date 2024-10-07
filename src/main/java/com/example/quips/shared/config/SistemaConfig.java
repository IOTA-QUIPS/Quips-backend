package com.example.quips.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sistema")
public class SistemaConfig {

    private int tokensIniciales;
    private int tokensPorJugador;
    private int[] cuotasPorFase;

    // Getters y Setters

    public int getTokensIniciales() {
        return tokensIniciales;
    }

    public void setTokensIniciales(int tokensIniciales) {
        this.tokensIniciales = tokensIniciales;
    }

    public int getTokensPorJugador() {
        return tokensPorJugador;
    }

    public void setTokensPorJugador(int tokensPorJugador) {
        this.tokensPorJugador = tokensPorJugador;
    }

    public int[] getCuotasPorFase() {
        return cuotasPorFase;
    }

    public void setCuotasPorFase(int[] cuotasPorFase) {
        this.cuotasPorFase = cuotasPorFase;
    }
}
