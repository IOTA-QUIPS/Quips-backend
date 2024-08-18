package com.example.quips.service;

import com.example.quips.config.SistemaConfig;
import com.example.quips.model.User;
import com.example.quips.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Sistema {

    private final SistemaConfig sistemaConfig;
    private int faseActual = 1;
    private int jugadoresEnFase = 0;
    private int transaccionesEnFase = 0;
    private final List<User> jugadores = new ArrayList<>(); // Lista para gestionar los jugadores


    @Autowired
    private UserRepository userRepository;  // Asegúrate de que esto esté presente en la clase Sistema

    @Autowired
    public Sistema(SistemaConfig sistemaConfig) {
        this.sistemaConfig = sistemaConfig;
    }

    public int getJugadoresEnFase() {
        return jugadoresEnFase;
    }

    public int getTransaccionesEnFase() {
        return transaccionesEnFase;
    }

    public int getFaseActual() {
        return faseActual;
    }

    // Agregar un nuevo jugador al sistema
    public void agregarJugador(User user) {
        System.out.println("Intentando agregar jugador: " + user.getUsername() + ". Jugadores en fase actual: " + jugadoresEnFase);

        // Verificar si se ha alcanzado el límite de jugadores para la fase actual
        if (jugadoresEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            System.out.println("Límite de jugadores alcanzado para Fase " + faseActual + ". Verificando si se puede transicionar de fase...");
            verificarTransicionDeFase();
        }

        // Si aún es posible agregar jugadores, agrégalo
        if (jugadoresEnFase < sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            jugadores.add(user);
            jugadoresEnFase++;
            System.out.println("Jugador " + user.getUsername() + " agregado a la Fase " + faseActual + ". Jugadores en fase ahora: " + jugadoresEnFase);
        } else {
            System.out.println("No se puede agregar más jugadores hasta que se transicione de fase.");
        }
    }

    // Registrar una nueva transacción
    public void registrarTransaccion() {
        transaccionesEnFase++;
        System.out.println("Transacción registrada. Transacciones en fase actual: " + transaccionesEnFase);

        // Verificar si se ha alcanzado el límite de transacciones para la fase actual
        if (transaccionesEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            System.out.println("Límite de transacciones alcanzado para Fase " + faseActual + ". Verificando si se puede transicionar de fase...");
            verificarTransicionDeFase();
        }
    }

    // Verifica si ambas condiciones para la transición de fase se han cumplido
    private void verificarTransicionDeFase() {
        if (jugadoresEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]
                && transaccionesEnFase >= sistemaConfig.getCuotasPorFase()[faseActual - 1]) {
            System.out.println("Condiciones cumplidas para transicionar a la siguiente fase.");
            transicionarFase();
        } else {
            System.out.println("Aún no se han cumplido todas las condiciones para la transición. Jugadores: "
                    + jugadoresEnFase + ", Transacciones: " + transaccionesEnFase);
        }
    }

    // Método para gestionar la transición entre fases
    private void transicionarFase() {
        // Verificar si se puede avanzar a la siguiente fase
        if (faseActual < sistemaConfig.getCuotasPorFase().length) {
            System.out.println("Distribuyendo recompensas para la Fase " + faseActual);
            distribuirRecompensas();

            // Avanzar a la siguiente fase
            faseActual++;
            System.out.println("Transición completada: Ahora en la Fase " + faseActual);

            // Ajustar el contador de jugadores después de la transición
            jugadoresEnFase = 0;  // Reiniciar el contador de jugadores para la nueva fase
            System.out.println("Jugadores en fase después de la transición: " + jugadoresEnFase);

            // Reiniciar el contador de transacciones
            transaccionesEnFase = 0;
        } else {
            System.out.println("No hay más fases disponibles.");
        }
    }

    // Método para distribuir recompensas a los jugadores más activos
    private void distribuirRecompensas() {
        System.out.println("Distribuyendo recompensas...");
        jugadores.sort((j1, j2) -> Double.compare(j2.getCoins(), j1.getCoins())); // Ordenar por actividad (coins)

        if (jugadores.size() >= 3) {
            jugadores.get(0).setCoins(jugadores.get(0).getCoins() + calcularRecompensa(3.0));
            userRepository.save(jugadores.get(0));  // Guardar cambios en la base de datos
            System.out.println("Recompensa de 3% distribuida a: " + jugadores.get(0).getUsername());

            jugadores.get(1).setCoins(jugadores.get(1).getCoins() + calcularRecompensa(2.0));
            userRepository.save(jugadores.get(1));  // Guardar cambios en la base de datos
            System.out.println("Recompensa de 2% distribuida a: " + jugadores.get(1).getUsername());

            jugadores.get(2).setCoins(jugadores.get(2).getCoins() + calcularRecompensa(1.0));
            userRepository.save(jugadores.get(2));  // Guardar cambios en la base de datos
            System.out.println("Recompensa de 1% distribuida a: " + jugadores.get(2).getUsername());

            System.out.println("Recompensas distribuidas a los jugadores más activos en la Fase " + (faseActual - 1));
        } else {
            System.out.println("No hay suficientes jugadores para distribuir recompensas.");
        }
    }


    // Método para calcular la recompensa basada en el porcentaje y los tokens emitidos en la fase
    private double calcularRecompensa(double porcentaje) {
        double tokensEmitidosEnFase = sistemaConfig.getCuotasPorFase()[faseActual - 1] * sistemaConfig.getTokensPorJugador();
        return (tokensEmitidosEnFase * porcentaje) / 100.0;
    }
}
