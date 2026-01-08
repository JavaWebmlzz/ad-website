// AdServiceImpl.java
package com.example.adwebsite.service.impl;

import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.entity.Advertisement;
import com.example.adwebsite.mapper.AdMapper;
import com.example.adwebsite.service.AdService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    @Value("${app.upload-url}")   // ① 注入外网前缀
    private String uploadUrlPrefix;
    private final AdMapper adMapper;
    private final Random random = new Random();

    public AdServiceImpl(AdMapper adMapper) {
        this.adMapper = adMapper;
    }

    @Override
    public List<Advertisement> getAllAds() {
        return adMapper.findAll();
    }

    @Override
    public Advertisement getAdById(Integer id) {
        return adMapper.findById(id);
    }

    @Override
    public Advertisement createAd(Advertisement ad) {
        if (ad.getDuration() == null) {
            ad.setDuration(15); // 默认15秒
        }
        Integer id = adMapper.insert(ad);
        ad.setId(id);
        return ad;
    }

    @Override
    public Advertisement updateAd(Integer id, Advertisement ad) {
        ad.setId(id);
        adMapper.update(ad);
        return adMapper.findById(id);
    }

    @Override
    public void deleteAd(Integer id) {
        adMapper.delete(id);
    }

    @Override
    public Advertisement getRandomAd() {
        List<Advertisement> ads = adMapper.findAll();
        if (ads.isEmpty()) {
            return null;
        }
        int index = random.nextInt(ads.size());
        Advertisement ad = ads.get(index);
        adMapper.incrementViews(ad.getId());
        return ad;
    }

    @GetMapping("/api/ads/{id}")
    public Advertisement getById(@PathVariable Integer id) {
        Advertisement ad = adMapper.findById(id);
        if (ad == null) {                      // 查不到抛 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "广告不存在");
        }
        // 需要顺带 +1 浏览量就调一行
        adMapper.incrementViews(id);
        return ad;
    }

    @Override
    public void recordView(Integer id) {
        adMapper.incrementViews(id);
    }

    @Override
    public void recordClick(Integer id) {
        adMapper.incrementClicks(id);
    }



    // ---------------------------------------------------
    /** ② 通用：把实体转成 VO 并拼视频完整地址 **/
    private AdvertisementVo toVo(Advertisement ad) {
        AdvertisementVo vo = new AdvertisementVo();
        vo.setId(ad.getId());
        vo.setTitle(ad.getTitle());
        vo.setDescription(ad.getDescription());
        vo.setDuration(ad.getDuration());
        vo.setViews(ad.getViews());
        vo.setClicks(ad.getClicks());
        vo.setIsActive(ad.getIsActive());
        // 关键：拼完整 URL
        vo.setVideoFullUrl(uploadUrlPrefix + ad.getVideoUrl());
        return vo;
    }

    /** ③ 提供给 Controller 的接口 **/
    @Override
    public AdvertisementVo getAdVoById(Integer id) {
        Advertisement ad = adMapper.findById(id);
        if (ad == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "广告不存在");
        }
        adMapper.incrementViews(id);
        return toVo(ad);
    }

    @Override
    public AdvertisementVo getRandomAdVo() {
        Advertisement ad = getRandomAd(); // 复用原逻辑
        return ad == null ? null : toVo(ad);
    }


    @Override
    public List<AdvertisementVo> getAdsByVideoPrefix(String prefix, int limit) {
        List<Advertisement> list = adMapper.findByVideoPrefix(prefix);
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(list);                 // 打乱
        return list.stream()
                .limit(limit)
                .map(this::toVo)                // 复用你已有的实体→VO
                .collect(Collectors.toList());
    }

}