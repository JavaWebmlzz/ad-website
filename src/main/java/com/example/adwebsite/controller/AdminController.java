// AdminController.java - 管理后台API
package com.example.adwebsite.controller;

import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.entity.Advertisement;
import com.example.adwebsite.service.AdService;
import com.example.adwebsite.service.impl.AdServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ads")
public class AdminController {

    @Autowired
    private AdServiceImpl adService;   // 注意用实现类才能调到 Vo 方法
    //private final AdService adService;
    private final String uploadDir = "uploads/ads/";

    public AdminController(AdService adService) {
        this.adService = (AdServiceImpl) adService;
        // 创建上传目录
        new File(uploadDir).mkdirs();
    }

    @GetMapping
    public ResponseEntity<List<Advertisement>> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Advertisement> getAdById(@PathVariable Integer id) {
//        return ResponseEntity.ok(adService.getAdById(id));
//    }

    @PostMapping
    public ResponseEntity<Advertisement> createAd(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile videoFile,
            @RequestParam(defaultValue = "15") Integer duration) throws IOException {

        // 调试用
        System.out.println(">>> 进入createAd，文件大小=" + videoFile.getSize());

        // 保存视频文件
        //String filename = UUID.randomUUID().toString() + "_" + videoFile.getOriginalFilename();
        String filename = videoFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + filename);
        Files.copy(videoFile.getInputStream(), filePath);

        // 调试用
        System.out.println(">>> 绝对路径=" + filePath.toAbsolutePath());

        Advertisement ad = new Advertisement();
        ad.setTitle(title);
        ad.setDescription(description);
        //ad.setVideoUrl("/uploads/ads/" + filename);
        ad.setVideoUrl("/" + uploadDir + filename);
        ad.setDuration(duration);

        return ResponseEntity.ok(adService.createAd(ad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Advertisement> updateAd(
            @PathVariable Integer id,
            @RequestBody Advertisement ad) {
        return ResponseEntity.ok(adService.updateAd(id, ad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {
        // 调试用
        System.out.println(">>> deleteAd，文件 id=" + id);

        // 1. 先把记录查出来，拿到 videoUrl
        Advertisement ad = adService.getAdById(id);
        String fileName = ad.getVideoUrl();   // 形如 "/uploads/ads/xxx.mp4"
        Path filePath = Paths.get(System.getProperty("user.dir"), fileName);

        // 2. 删库
        adService.deleteAd(id);

        // 3. 删文件
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();
    }


    // 单条查询 + 完整视频地址
    @GetMapping("/{id}")
    public AdvertisementVo getById(@PathVariable Integer id) {
        return adService.getAdVoById(id);
    }

    // 随机一条 + 完整视频地址
    @GetMapping("/random")
    public AdvertisementVo randomAd() {
        return adService.getRandomAdVo();
    }
}

