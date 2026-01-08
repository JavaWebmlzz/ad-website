package com.example.adwebsite.dto;

import com.example.adwebsite.entity.Advertisement;
import lombok.Data;

@Data
public class AdvertisementVo {
    private Integer id;
    private String adType;
    private String title;
    private String description;
    private Integer duration;
    private Integer views;
    private Integer clicks;
    private Boolean isActive;
    private String videoFullUrl;   // 关键：完整可播放地址
}