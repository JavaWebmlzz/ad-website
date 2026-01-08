// ApiController.java - 对外提供广告的API
package com.example.adwebsite.controller;

import com.example.adwebsite.entity.Advertisement;
import com.example.adwebsite.service.AdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ads")
public class ApiController {

    private final AdService adService;

    public ApiController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping("/admin/random")
    public ResponseEntity<Advertisement> getRandomAd() {
        Advertisement ad = adService.getRandomAd();
        if (ad == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ad);
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<Void> recordClick(@PathVariable Integer id) {
        adService.recordClick(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> recordView(@PathVariable Integer id) {
        adService.recordView(id);
        return ResponseEntity.ok().build();
    }
}