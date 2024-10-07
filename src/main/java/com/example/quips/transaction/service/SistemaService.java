package com.example.quips.transaction.service;

import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.authentication.domain.model.User;
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
        if (jugadoresEnFase < sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            jugadores.add(user);
            jugadoresEnFase++;
        }
    }

    public void registrarTransaccion() {
        transaccionesEnFase++;
    }

    public boolean debeTransicionar() {
        return jugadoresEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]
                && transaccionesEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1];
    }

    public void transicionarFase() {
        if (faseActual < sistemaConfig.getCuotasPorFase().length) {
            faseActual++;
            jugadoresEnFase = 0;
            transaccionesEnFase = 0;
            // No limpiar la lista de jugadores, a menos que esté seguro de que estos datos no son necesarios en la próxima fase.
            // jugadores.clear();  -> Esto puede ser opcional dependiendo de la implementación y si necesitas mantener los datos de la fase anterior.
        }
    }

    public List<User> getJugadores() {
        return new ArrayList<>(jugadores);
    }
}

