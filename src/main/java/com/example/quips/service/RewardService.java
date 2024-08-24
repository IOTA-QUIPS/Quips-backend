package com.example.quips.service;

import com.example.quips.config.SistemaConfig;
import com.example.quips.model.User;
import com.example.quips.model.Wallet;
import com.example.quips.repository.TransactionRepository;
import com.example.quips.repository.WalletRepository;
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

    public void distribuirRecompensas(List<User> jugadores, int faseActual) {
        List<User> jugadoresConTransaccionesEnFase = jugadores.stream()
                .filter(jugador -> haRealizadoTransaccionesEnFase(jugador, faseActual))
                .sorted((j1, j2) -> Integer.compare(contarTransaccionesEnFase(j2, faseActual), contarTransaccionesEnFase(j1, faseActual)))
                .collect(Collectors.toList());

        if (jugadoresConTransaccionesEnFase.isEmpty()) {
            System.out.println("No hay suficientes jugadores activos en esta fase para distribuir recompensas.");
            return;
        }

        for (int i = 0; i < Math.min(3, jugadoresConTransaccionesEnFase.size()); i++) {
            User jugador = jugadoresConTransaccionesEnFase.get(i);
            double porcentajeRecompensa = (i == 0) ? 3.0 : (i == 1) ? 2.0 : 1.0;
            double recompensa = calcularRecompensaFase(porcentajeRecompensa, faseActual);

            Wallet wallet = jugador.getWallet();
            walletService.addCoins(wallet, recompensa);

            System.out.println("Recompensa de " + porcentajeRecompensa + "% distribuida a: " + jugador.getUsername());
        }
    }

    private boolean haRealizadoTransaccionesEnFase(User jugador, int faseActual) {
        return transactionRepository.countBySenderWalletIdAndFase(jugador.getWallet().getId(), faseActual) > 0 ||
                transactionRepository.countByReceiverWalletIdAndFase(jugador.getWallet().getId(), faseActual) > 0;
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
