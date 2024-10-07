package com.example.quips.chat.rest;

import com.example.quips.authentication.domain.model.User;
import com.example.quips.authentication.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "API para verificar contactos registrados")
public class ContactController {

    private final UserRepository userRepository;

    public ContactController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint para verificar contactos registrados en la aplicación
    @PostMapping("/check")
    public ResponseEntity<List<User>> checkRegisteredContacts(@RequestBody List<String> phoneNumbers) {
        // Buscar los usuarios cuyos números de teléfono están registrados en la base de datos
        List<User> registeredContacts = userRepository.findByPhoneNumberIn(phoneNumbers);

        return ResponseEntity.ok(registeredContacts);
    }
}