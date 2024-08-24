package com.example.quips.service;

import com.example.quips.config.SistemaConfig;
import com.example.quips.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

    private final SistemaConfig sistemaConfig;
    private int faseActual = 1;
    private int jugadoresEnFase = 0;
    private int transaccionesEnFase = 0;
    private final List<User> jugadores = new ArrayList<>();

    public SistemaService(SistemaConfig sistemaConfig) {
        this.sistemaConfig = sistemaConfig;
    }

    public int getJugadoresEnFase() {
        return jugadoresEnFase;
    }

    public int getFaseActual() {
        return faseActual;
    }

    public void agregarJugador(User user) {
        if (jugadoresEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            verificarTransicionDeFase();
        }

        if (jugadoresEnFase < sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            jugadores.add(user);
            jugadoresEnFase++;
        }
    }

    public void registrarTransaccion() {
        transaccionesEnFase++;

        if (transaccionesEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            verificarTransicionDeFase();
        }
    }

    private void verificarTransicionDeFase() {
        if (jugadoresEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]
                && transaccionesEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            transicionarFase();
        }
    }

    private void transicionarFase() {
        if (faseActual < sistemaConfig.getCuotasPorFase().length) {
            faseActual++;
            jugadoresEnFase = 0;
            transaccionesEnFase = 0;
        }
    }
}
