package com.example.adwebsite.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Advertisement {
    private Integer id;
    private String title;
    private String description;
    private String videoUrl;
    private Integer duration;
    private Integer clicks;
    private Integer views;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}