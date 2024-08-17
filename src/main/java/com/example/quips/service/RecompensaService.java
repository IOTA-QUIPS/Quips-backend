package com.example.quips.service;

import com.example.quips.model.User;
import com.example.quips.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecompensaService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Distribuye las recompensas a los jugadores más activos.
     *
     * @param jugadores Lista de todos los jugadores.
     */
    public void distribuirRecompensas(List<User> jugadores) {
        // Ordenar los jugadores por la cantidad de transacciones (o monedas, dependiendo de tu lógica)
        jugadores.sort((j1, j2) -> Integer.compare(j2.getCoins(), j1.getCoins()));

        // Verificamos si hay al menos 3 jugadores para distribuir recompensas
        if (jugadores.size() >= 3) {
            User jugador1 = jugadores.get(0);
            User jugador2 = jugadores.get(1);
            User jugador3 = jugadores.get(2);

            // Recompensas basadas en un porcentaje predefinido (ajustar según tu lógica)
            int recompensa1 = calcularRecompensa(3);
            int recompensa2 = calcularRecompensa(2);
            int recompensa3 = calcularRecompensa(1);

            // Actualizar los saldos de los jugadores
            jugador1.setCoins(jugador1.getCoins() + recompensa1);
            jugador2.setCoins(jugador2.getCoins() + recompensa2);
            jugador3.setCoins(jugador3.getCoins() + recompensa3);

            // Guardar los cambios en la base de datos
            userRepository.save(jugador1);
            userRepository.save(jugador2);
            userRepository.save(jugador3);

            System.out.println("Recompensas distribuidas: " +
                    "Jugador 1: " + recompensa1 + " tokens, " +
                    "Jugador 2: " + recompensa2 + " tokens, " +
                    "Jugador 3: " + recompensa3 + " tokens.");
        } else {
            System.out.println("No hay suficientes jugadores para distribuir recompensas.");
        }
    }

    /**
     * Calcula la cantidad de recompensa basada en la posición del jugador.
     *
     * @param posicion Posición del jugador (1 para el primer lugar, 2 para el segundo, etc.)
     * @return La cantidad de tokens a otorgar como recompensa.
     */
    private int calcularRecompensa(int posicion) {
        int recompensa = switch (posicion) {
            case 1 -> 3;  // Primer lugar recibe el 3% de los tokens disponibles para recompensas
            case 2 -> 2;  // Segundo lugar recibe el 2%
            case 3 -> 1;  // Tercer lugar recibe el 1%
            default -> 0;  // Otros lugares no reciben recompensa
        };
        return recompensa;
    }
}
