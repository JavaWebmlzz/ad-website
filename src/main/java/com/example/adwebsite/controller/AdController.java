package com.example.adwebsite.controller;

import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdService adService;   // Spring 会自动注入 AdServiceImpl

    @GetMapping("/{id}")
    public AdvertisementVo getById(@PathVariable Integer id) {
        return adService.getAdVoById(id);   // 待会要在 Service 里加这个方法
    }

    @GetMapping("/random")
    public AdvertisementVo randomAd() {
        return adService.getRandomAdVo();   // 同样待会加
    }

    @GetMapping("/randomByPrefix")
    public List<AdvertisementVo> randomByPrefix(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "1") int limit) {
        return adService.getAdsByVideoPrefix(prefix, limit);
    }
}