package com.example.quips.analytics.service;

import com.example.quips.analytics.dto.UserActivityDTO;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    @Autowired
    private TopUsersTransactionService topUsersTransactionService;

    @Autowired
    private ReferralRewardsService referralRewardsService;

    @Autowired
    private UserRepository userRepository;  // Inyección de UserRepository para acceder a los usuarios

    public UserActivityDTO calculateUserActivity(Long userId) {
        int points = 0;

        // Obtener el usuario desde el repositorio
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para ID: " + userId));

        // Obtener la cantidad de referidos
        int referidos = referralRewardsService.getReferralsByUserId(userId).size();
        points += calculatePointsForReferrals(referidos);

        // Obtener las transacciones enviadas
        int transaccionesEnviadas = topUsersTransactionService.getSentTransactionsByUserId(userId).size();
        points += calculatePointsForTransactions(transaccionesEnviadas);

        // Obtener las transacciones recibidas
        int transaccionesRecibidas = topUsersTransactionService.getReceivedTransactionsByUserId(userId).size();
        points += calculatePointsForTransactions(transaccionesRecibidas);

        // Determinar nivel de actividad
        String nivelActividad = determineActivityLevel(points);

        // Crear y retornar el DTO con los nuevos campos
        return new UserActivityDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                referidos,
                transaccionesEnviadas,
                transaccionesRecibidas,
                nivelActividad
        );
    }

    // Método para calcular la actividad de todos los usuarios
    public List<UserActivityDTO> calculateAllUsersActivity() {
        return userRepository.findAll().stream()
                .map(user -> calculateUserActivity(user.getId()))
                .collect(Collectors.toList());
    }

    private int calculatePointsForReferrals(int referidos) {
        if (referidos > 2) return 2;
        else if (referidos >= 1) return 1;
        else return 0;
    }

    private int calculatePointsForTransactions(int transacciones) {
        if (transacciones > 5) return 2;
        else if (transacciones >= 2) return 1;
        else return 0;
    }

    private String determineActivityLevel(int points) {
        if (points >= 5) return "Activo";
        else if (points >= 3) return "Normal";
        else return "No Activo";
    }
}
