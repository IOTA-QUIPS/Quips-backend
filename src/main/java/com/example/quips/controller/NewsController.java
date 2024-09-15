package com.example.quips.controller;

import com.example.quips.model.ERole;
import com.example.quips.model.News;
import com.example.quips.model.User;
import com.example.quips.repository.NewsRepository;
import com.example.quips.repository.UserRepository;
import com.example.quips.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Obtener todas las noticias (para cualquier usuario)
    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        return ResponseEntity.ok(newsRepository.findAll());
    }

    // Crear una noticia (solo para admin)
    @PostMapping("/add")
    public ResponseEntity<?> createNews(@RequestHeader("Authorization") String token, @RequestBody News news) {
        // Verificar si es administrador
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para agregar noticias.");
        }

        news.setPublishedAt(LocalDateTime.now());
        newsRepository.save(news);
        return ResponseEntity.ok("Noticia agregada exitosamente.");
    }

    // Editar una noticia (solo para admin)
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editNews(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody News updatedNews) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para editar noticias.");
        }

        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("Noticia no encontrada"));
        news.setTitle(updatedNews.getTitle());
        news.setContent(updatedNews.getContent());
        news.setPublishedAt(LocalDateTime.now());
        newsRepository.save(news);
        return ResponseEntity.ok("Noticia actualizada.");
    }

    // Eliminar una noticia (solo para admin)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para eliminar noticias.");
        }

        newsRepository.deleteById(id);
        return ResponseEntity.ok("Noticia eliminada.");
    }

    // Método para verificar si el usuario es admin
    private boolean isAdmin(String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));
    }
}
