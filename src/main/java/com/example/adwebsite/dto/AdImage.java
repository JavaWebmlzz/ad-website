package com.example.adwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdImage {
    private Integer id;
    private String adType;      // 固定值 "image"
    private String title;
    private String description;
    private String imageUrl;    // 库内路径 /uploads/images/xxx.jpg
    private Integer views;
    private Integer clicks;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
