package com.example.quips.controller;

import com.example.quips.config.SistemaConfig;
import com.example.quips.dto.CreateUserRequest;
import com.example.quips.dto.LoginRequest;
import com.example.quips.model.*;
import com.example.quips.repository.RoleRepository;
import com.example.quips.repository.UserRepository;
import com.example.quips.repository.WalletRepository;
import com.example.quips.service.SistemaService;
import com.example.quips.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API para gestionar usuarios")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WalletRepository walletRepository; // Inyectar WalletRepository

    @Autowired
    private RoleRepository roleRepository; // Inyectar RoleRepository

    @Autowired
    private SistemaConfig sistemaConfig;  // Inyección de SistemaConfig

    @Autowired
    private BovedaCero bovedaCero;  // Inyección de BovedaCero

    @Autowired
    private SistemaService sistema;  // Inyección del servicio Sistema

    // Obtener todos los usuarios
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    // Endpoint de login
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Comparar contraseñas (recomendable usar hash en producción)
            if (user.getPassword().equals(request.getPassword())) {
                // Generar el token (suponiendo que tienes una clase JwtUtil para manejar JWT)
                String token = jwtUtil.generateToken(user.getUsername());

                // Devolver el token en la respuesta
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @GetMapping("/me")
    public ResponseEntity<?> getMyUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el nombre de usuario del token
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

            // Buscar al usuario por nombre de usuario
            Optional<User> user = userRepository.findByUsername(username);

            // Retornar los datos del usuario si es encontrado
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            // Manejar cualquier excepción relacionada con el token JWT
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido o expirado.");
        }
    }



    // Obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo usuario
    @CrossOrigin(origins = "*") // O especifica el origen permitido
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        int jugadoresEnFase = sistema.getJugadoresEnFase();
        int cuotaFaseActual = sistemaConfig.getCuotasPorFase()[sistema.getFaseActual() - 1];

        if (jugadoresEnFase >= cuotaFaseActual) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede agregar más jugadores hasta que se transicione de fase.");
        }

        int tokensAsignados = sistemaConfig.getTokensPorJugador();
        long tokensDisponibles = bovedaCero.getTokens();

        if (tokensDisponibles >= tokensAsignados) {
            Wallet wallet = new Wallet();
            wallet.setCoins(tokensAsignados);

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setWallet(wallet);

            bovedaCero.restarTokens(tokensAsignados);
            sistema.agregarJugador(user);

            // Asignar el rol USER por defecto
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado."));
            user.getRoles().add(userRole);

            walletRepository.save(wallet);
            userRepository.save(user);

            long tokensEnCirculacion = sistemaConfig.getTokensIniciales() - bovedaCero.getTokens();
            System.out.println("Usuario " + user.getUsername() + " ha sido creado con " + tokensAsignados + " tokens.");

            String responseMessage = "Usuario creado exitosamente. Tokens en circulación: " + tokensEnCirculacion;
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficientes tokens disponibles.");
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Verificar que el usuario autenticado es administrador
        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para hacer esto.");
        }

        // Asignar el rol ADMIN al usuario especificado
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado."));
        user.getRoles().add(adminRole);

        userRepository.save(user);
        return ResponseEntity.ok("El usuario ha sido promovido a ADMIN.");
    }

    @GetMapping("/admin/overview")
    public ResponseEntity<?> getAdminOverview(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Verificar que el usuario tiene el rol ADMIN
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para acceder a esta información.");
        }

        // Lógica para mostrar el "overview" para administradores
        return ResponseEntity.ok("Datos del administrador");
    }


    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            user.setFirstName(userDetails.getFirstName()); // Nuevo campo
            user.setLastName(userDetails.getLastName());

            // Recuerda cifrar la contraseña en un entorno real

            Wallet wallet = user.getWallet();
            wallet.setCoins(userDetails.getWallet().getCoins());
            walletRepository.save(wallet); // Actualizar la wallet

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
