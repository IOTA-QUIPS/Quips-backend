package com.example.quips.authentication.rest;

import com.example.quips.authentication.domain.model.ERole;
import com.example.quips.authentication.domain.model.Role;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.shared.config.SistemaConfig;
import com.example.quips.authentication.dto.CreateUserRequest;
import com.example.quips.authentication.dto.LoginRequest;
import com.example.quips.authentication.dto.UserDTO;
import com.example.quips.authentication.dto.SetPinRequest;
import com.example.quips.authentication.repository.RoleRepository;
import com.example.quips.authentication.repository.UserRepository;
import com.example.quips.transaction.repository.VerificationTokenRepository;
import com.example.quips.transaction.repository.WalletRepository;
import com.example.quips.authentication.service.EmailService;
import com.example.quips.transaction.service.SistemaService;
import com.example.quips.shared.util.CodeGenerator;
import com.example.quips.shared.util.JwtUtil;
import com.example.quips.transaction.domain.model.BovedaCero;
import com.example.quips.authentication.dto.VerificationToken;
import com.example.quips.transaction.domain.model.Wallet;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") // O especifica el origen permitido
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
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository; // Inyectar WalletRepository



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

            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cuenta no activada.");
            }

            if (user.getPassword().equals(request.getPassword()) || user.getSixDigitPin().equals(request.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña o clave de 6 dígitos incorrecta.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/me")
    public ResponseEntity<?> getMyUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el nombre de usuario del token
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

            // Buscar al usuario por nombre de usuario
            Optional<User> user = userRepository.findByUsername(username);

            // Retornar los datos del usuario si es encontrado
            if (user.isPresent()) {
                User foundUser = user.get();

                // Obtener los roles del usuario (si los tienes implementados)
                Set<String> roles = foundUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet());

                // Obtener los coins de la wallet
                double coins = (foundUser.getWallet() != null) ? foundUser.getWallet().getCoins() : 0.0;

                // Crear el DTO con la información del usuario, incluyendo las monedas
                UserDTO userDTO = new UserDTO(
                                        foundUser.getId(),  // Agregar el ID aquí
                                        foundUser.getUsername(),
                                        foundUser.getSixDigitPin(),
                                        foundUser.getFirstName(),
                                        foundUser.getLastName(),
                                        foundUser.getEmail(),
                                        foundUser.getPhoneNumber(),
                                        foundUser.getAccountNumber(),
                                        foundUser.getReferralCode(),


                        roles,
                        coins,
                        foundUser.isActive()  // Pasar las monedas desde la wallet del usuario
                                );

                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no válido o expirado.");
        }
    }

    @PostMapping("/setPin")
    public ResponseEntity<?> setSixDigitPin(@RequestHeader("Authorization") String token, @RequestBody SetPinRequest request) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSixDigitPin(request.getSixDigitPin()); // Asigna el PIN desde el DTO
            userRepository.save(user);
            return ResponseEntity.ok("Clave de 6 dígitos configurada con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
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
        // Verificar si el username ya está en uso
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre de usuario ya está en uso.");
        }

        // Verificar si el email ya está en uso
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya está en uso.");
        }

        // Verificar si el número de teléfono ya está en uso
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El número de teléfono ya está en uso.");
        }

        int jugadoresEnFase = sistema.getJugadoresEnFase();
        int cuotaFaseActual = sistemaConfig.getCuotasPorFase()[sistema.getFaseActual() - 1];

        // Verificar si se ha alcanzado el límite de jugadores en la fase
        if (jugadoresEnFase >= cuotaFaseActual) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede agregar más jugadores hasta que se transicione de fase.");
        }

        // Verificar si hay tokens disponibles para asignar al nuevo usuario
        int tokensAsignados = sistemaConfig.getTokensPorJugador();
        long tokensDisponibles = bovedaCero.getTokens();

        if (tokensDisponibles < tokensAsignados) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficientes tokens disponibles.");
        }

        // **Verificar si se proporcionó un código de referido**
        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Optional<User> referrerOptional = userRepository.findByReferralCode(request.getReferralCode());

            // Si el código de referido no es válido, detener el registro
            if (!referrerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código de referido no válido.");
            }
        }

        // Crear la wallet del usuario con los tokens asignados
        Wallet wallet = new Wallet();
        wallet.setCoins(tokensAsignados);

        // Crear el nuevo usuario con los datos proporcionados
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());           // Establece el email
        user.setPhoneNumber(request.getPhoneNumber()); // Establece el número de celular
        user.setWallet(wallet);
        user.setActive(false); // Cuenta no activa hasta verificación

        // **Generar el código de referido para el nuevo usuario**
        String referralCode = CodeGenerator.generateReferralCode();
        user.setReferralCode(referralCode);

        // Restar los tokens asignados del almacén
        bovedaCero.restarTokens(tokensAsignados);
        sistema.agregarJugador(user);

        // Si el código de referido es válido, premiar al referente
        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Optional<User> referrerOptional = userRepository.findByReferralCode(request.getReferralCode());

            if (referrerOptional.isPresent()) {
                User referrer = referrerOptional.get();

                // Añadir 3 monedas al usuario que proporcionó el código de referido
                referrer.getWallet().setCoins(referrer.getWallet().getCoins() + 3);
                walletRepository.save(referrer.getWallet());

                // Restar 3 monedas de BovedaCero
                bovedaCero.restarTokens(3);
            }
        }

        // Asignar el rol USER por defecto
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado."));
        user.getRoles().add(userRole);

        walletRepository.save(wallet);
        userRepository.save(user);

        // Generar y guardar el token de verificación
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        // Enviar correo con el enlace de verificación
        String verificationLink = "http://localhost:8080/api/users/verify?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);

        long tokensEnCirculacion = sistemaConfig.getTokensIniciales() - bovedaCero.getTokens();
        System.out.println("Usuario " + user.getUsername() + " ha sido creado con " + tokensAsignados + " tokens.");

        String responseMessage = "Usuario creado exitosamente. Tokens en circulación: " + tokensEnCirculacion;
        return ResponseEntity.ok(responseMessage);
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
            user.setEmail(userDetails.getEmail());             // Actualiza el email
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setActive(userDetails.isActive());// Actualiza el número de celular


            // Lógica para actualizar los roles
            if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
                // Elimina los roles actuales y añade los nuevos
                user.getRoles().clear();
                for (Role role : userDetails.getRoles()) {
                    Role existingRole = roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + role.getName()));
                    user.getRoles().add(existingRole);
                }
            }

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


    @GetMapping("/verify")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);

        if (verificationTokenOpt.isPresent()) {
            VerificationToken verificationToken = verificationTokenOpt.get();
            User user = verificationToken.getUser();

            // Verificar si el token ha expirado
            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado.");
            }

            // Activar la cuenta del usuario
            user.setActive(true);
            userRepository.save(user);

            // Eliminar el token de verificación
            verificationTokenRepository.delete(verificationToken);

            return ResponseEntity.ok("Cuenta activada con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de verificación inválido.");
        }
    }


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