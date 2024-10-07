package com.example.quips.news.service;

import com.example.quips.news.domain.model.News;
import com.example.quips.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // Obtener todas las noticias
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    // Crear una nueva noticia
    public News createNews(String title, String content, String imageUrl) {
        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setImageUrl(imageUrl);
        news.setPublishedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    // En el servicio, añade este método si no existe
    public News getNewsById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("Noticia no encontrada"));
    }


    // Editar una noticia existente
    public News updateNews(Long id, String title, String content, String imageUrl) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));
        news.setTitle(title);
        news.setContent(content);
        news.setImageUrl(imageUrl);
        news.setPublishedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    // Eliminar una noticia
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
