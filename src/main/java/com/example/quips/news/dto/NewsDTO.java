package com.example.quips.news.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NewsDTO {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime publishedAt;

}
