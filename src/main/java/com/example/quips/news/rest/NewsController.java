package com.example.quips.news.rest;

import com.example.quips.news.dto.NewsDTO;
import com.example.quips.authentication.domain.model.ERole;
import com.example.quips.news.domain.model.News;
import com.example.quips.authentication.domain.model.User;
import com.example.quips.news.service.NewsService;
import com.example.quips.shared.util.JwtUtil;
import com.example.quips.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Obtener todas las noticias (para cualquier usuario)
    @GetMapping
    public ResponseEntity<List<NewsDTO>> getAllNews() {
        List<News> newsList = newsService.getAllNews();
        List<NewsDTO> newsDTOList = newsList.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(newsDTOList);
    }

    // Crear una noticia (solo para admin)
    @PostMapping("/add")
    public ResponseEntity<?> createNews(@RequestHeader("Authorization") String token, @RequestBody NewsDTO newsDTO) {
        // Verificar si es administrador
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para agregar noticias.");
        }

        News news = newsService.createNews(newsDTO.getTitle(), newsDTO.getContent(), newsDTO.getImageUrl());
        return ResponseEntity.ok("Noticia agregada exitosamente.");
    }

    // Editar una noticia (solo para admin)
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editNews(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody NewsDTO newsDTO) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para editar noticias.");
        }

        News updatedNews = newsService.updateNews(id, newsDTO.getTitle(), newsDTO.getContent(), newsDTO.getImageUrl());
        return ResponseEntity.ok("Noticia actualizada.");
    }

    // Eliminar una noticia (solo para admin)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para eliminar noticias.");
        }

        newsService.deleteNews(id);
        return ResponseEntity.ok("Noticia eliminada.");
    }

    // Obtener noticia por id (para cualquier usuario)
    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable Long id) {
        News news = newsService.getNewsById(id);
        NewsDTO newsDTO = convertToDTO(news);
        return ResponseEntity.ok(newsDTO);
    }


    // Método para verificar si el usuario es admin
    private boolean isAdmin(String token) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));
    }

    // Conversión de News a NewsDTO
    private NewsDTO convertToDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setContent(news.getContent());
        newsDTO.setImageUrl(news.getImageUrl());
        newsDTO.setPublishedAt(news.getPublishedAt());
        return newsDTO;
    }
}
