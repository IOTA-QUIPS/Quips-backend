package com.example.quips.transaction.service;

import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardService {

    @Autowired
    private SistemaConfig sistemaConfig;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private SistemaService sistemaService;


    public void distribuirRecompensas() {
        int faseActual = sistemaService.getFaseActual();
        List<User> jugadores = sistemaService.getJugadores();

        // Obtener los jugadores activos en la fase actual
        List<User> jugadoresConTransaccionesEnFase = jugadores.stream()
                .filter(jugador -> haRealizadoTransaccionesEnFase(jugador, faseActual))
                .sorted((j1, j2) -> Integer.compare(contarTransaccionesEnFase(j2, faseActual), contarTransaccionesEnFase(j1, faseActual)))
                .collect(Collectors.toList());

        // Recompensas base
        double[] porcentajesRecompensa = {3.0, 2.0, 1.0};
        double[] recompensasAsignadas = new double[jugadoresConTransaccionesEnFase.size()];

        // Repartir recompensas considerando empates
        for (int i = 0; i < jugadoresConTransaccionesEnFase.size(); i++) {
            if (i < 3) {
                User jugador = jugadoresConTransaccionesEnFase.get(i);
                double recompensaActual = calcularRecompensaFase(porcentajesRecompensa[i], faseActual);

                // Verificar si hay empate en la posición 1
                if (i == 0 && i < jugadoresConTransaccionesEnFase.size() - 1 &&
                        contarTransaccionesEnFase(jugador, faseActual) == contarTransaccionesEnFase(jugadoresConTransaccionesEnFase.get(i + 1), faseActual)) {

                    // Si hay empate en la posición 1, dividir la recompensa del 3% y 2% entre los dos primeros jugadores
                    recompensaActual += calcularRecompensaFase(porcentajesRecompensa[i + 1], faseActual);
                    recompensaActual /= 2.0;

                    recompensasAsignadas[i] = recompensaActual;
                    recompensasAsignadas[i + 1] = recompensaActual;

                    System.out.println("Recompensas de 3% y 2% divididas entre: "
                            + jugador.getUsername() + " y " + jugadoresConTransaccionesEnFase.get(i + 1).getUsername());

                    i++; // Saltar al siguiente jugador ya que fue parte del empate

                } else if (i == 1 && i < jugadoresConTransaccionesEnFase.size() - 1 &&
                        contarTransaccionesEnFase(jugador, faseActual) == contarTransaccionesEnFase(jugadoresConTransaccionesEnFase.get(i + 1), faseActual)) {

                    // Si hay empate en la posición 2, dividir la recompensa del 2% y 1% entre los jugadores en las posiciones 2 y 3
                    recompensaActual += calcularRecompensaFase(porcentajesRecompensa[i + 1], faseActual);
                    recompensaActual /= 2.0;

                    recompensasAsignadas[i] = recompensaActual;
                    recompensasAsignadas[i + 1] = recompensaActual;

                    System.out.println("Recompensas de 2% y 1% divididas entre: "
                            + jugador.getUsername() + " y " + jugadoresConTransaccionesEnFase.get(i + 1).getUsername());

                    i++; // Saltar al siguiente jugador ya que fue parte del empate

                } else {
                    // No hay empate, asignar recompensa normal
                    recompensasAsignadas[i] = recompensaActual;

                    System.out.println("Recompensa de " + porcentajesRecompensa[i] + "% distribuida a: " + jugador.getUsername());
                }
            }
        }

        // Aplicar las recompensas a los jugadores usando WalletService
        for (int i = 0; i < jugadoresConTransaccionesEnFase.size(); i++) {
            User jugador = jugadoresConTransaccionesEnFase.get(i);
            if (recompensasAsignadas[i] > 0) {
                walletService.addCoins(jugador.getWallet(), recompensasAsignadas[i]);
            }
        }
    }

    private boolean haRealizadoTransaccionesEnFase(User jugador, int faseActual) {
        return contarTransaccionesEnFase(jugador, faseActual) > 0;
    }

    private int contarTransaccionesEnFase(User jugador, int faseActual) {
        return transactionRepository.countBySenderWalletIdAndFase(jugador.getWallet().getId(), faseActual) +
                transactionRepository.countByReceiverWalletIdAndFase(jugador.getWallet().getId(), faseActual);
    }

    private double calcularRecompensaFase(double porcentaje, int faseActual) {
        double tokensEmitidosEnFase = sistemaConfig.getCuotasPorFase()[faseActual - 1] * sistemaConfig.getTokensPorJugador();
        return (tokensEmitidosEnFase * porcentaje) / 100.0;
    }
}
