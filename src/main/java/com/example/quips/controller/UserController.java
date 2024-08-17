package com.example.quips.controller;

import com.example.quips.config.SistemaConfig;
import com.example.quips.model.BovedaCero;
import com.example.quips.model.User;
import com.example.quips.repository.UserRepository;
import com.example.quips.service.Sistema; // Importa el servicio Sistema
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API para gestionar usuarios")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SistemaConfig sistemaConfig;  // Inyección de SistemaConfig

    @Autowired
    private BovedaCero bovedaCero;  // Inyección de BovedaCero

    @Autowired
    private Sistema sistema;  // Inyección del servicio Sistema

    // Obtener todos los usuarios
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // Verificar si se ha alcanzado el límite de jugadores para la fase actual
        int jugadoresEnFase = sistema.getJugadoresEnFase();
        int transaccionesEnFase = sistema.getTransaccionesEnFase();
        int cuotaFaseActual = sistemaConfig.getCuotasPorFase()[sistema.getFaseActual() - 1];

        if (jugadoresEnFase >= cuotaFaseActual && transaccionesEnFase < cuotaFaseActual) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede agregar más jugadores hasta que se completen suficientes transacciones.");
        }

        // Proceder con la creación del usuario
        int tokensAsignados = sistemaConfig.getTokensPorJugador();
        long tokensDisponibles = bovedaCero.getTokens();

        if (tokensDisponibles >= tokensAsignados) {
            user.setCoins(tokensAsignados);
            bovedaCero.restarTokens(tokensAsignados);

            // Agregar el usuario al sistema para gestionar la fase
            sistema.agregarJugador(user);

            userRepository.save(user);

            long tokensEnCirculacion = sistemaConfig.getTokensIniciales() - bovedaCero.getTokens();

            // Mostrar mensaje en consola
            System.out.println("Usuario " + user.getUsername() + " ha sido creado con " + tokensAsignados + " tokens. Tokens en circulación: " + tokensEnCirculacion + ". Tokens restantes en Bóveda Cero: " + bovedaCero.getTokens());

            String responseMessage = "Usuario creado exitosamente. Tokens en circulación: " + tokensEnCirculacion + ". Tokens restantes en Bóveda Cero: " + bovedaCero.getTokens();
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficientes tokens disponibles en la Bóveda Cero para asignar.");
        }
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword()); // Recuerda cifrar la contraseña en un entorno real
            user.setWalletId(userDetails.getWalletId());
            user.setCoins(userDetails.getCoins());
            final User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
