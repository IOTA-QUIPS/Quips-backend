package com.example.quips.service;

import com.example.quips.model.User;
import java.util.List;

public class Sistema {
    private int faseActual = 1;
    private int maxJugadoresPorFase = 10;
    private int jugadoresEnFase = 0;
    private int transaccionesEnFase = 0;

    public void agregarJugador(User user, List<User> jugadores) {
        if (jugadoresEnFase < maxJugadoresPorFase) {
            jugadores.add(user);
            jugadoresEnFase++;
        } else {
            transicionarFase(jugadores);
        }
    }

    private void transicionarFase(List<User> jugadores) {
        faseActual++;
        jugadoresEnFase = 0;
        transaccionesEnFase = 0;

        maxJugadoresPorFase = switch (faseActual) {
            case 2 -> 20;
            case 3 -> 30;
            // Agregar más fases según sea necesario
            default -> 100;
        };

        distribuirRecompensas(jugadores);
    }

    public void registrarTransaccion() {
        transaccionesEnFase++;
        if (transaccionesEnFase >= maxJugadoresPorFase) {
            // Lógica para transicionar fase si se exceden las transacciones permitidas
        }
    }

    public void distribuirRecompensas(List<User> jugadores) {
        jugadores.sort((j1, j2) -> Integer.compare(j2.getCoins(), j1.getCoins()));

        if (jugadores.size() >= 3) {
            jugadores.get(0).setCoins(jugadores.get(0).getCoins() + calcularRecompensa(3));
            jugadores.get(1).setCoins(jugadores.get(1).getCoins() + calcularRecompensa(2));
            jugadores.get(2).setCoins(jugadores.get(2).getCoins() + calcularRecompensa(1));
        }
    }

    private int calcularRecompensa(int posicion) {
        int recompensa = switch (posicion) {
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 1;
            default -> 0;
        };
        return recompensa;
    }
}
