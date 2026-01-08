package com.example.adwebsite.controller;

import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.service.AdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/imageAds")
public class AdImageController {

    @Autowired
    private AdImageService imageService;

    // 根据前缀随机返回图片广告
    @GetMapping("/randomByPrefix")
    public List<AdvertisementVo> randomByPrefix(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "1") int limit) {
        return imageService.getImagesByPrefix(prefix, limit);
    }
}
