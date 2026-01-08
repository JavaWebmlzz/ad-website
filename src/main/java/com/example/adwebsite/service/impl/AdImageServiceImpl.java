package com.example.adwebsite.service.impl;

import com.example.adwebsite.dto.AdImage;
import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.mapper.AdImageMapper;
import com.example.adwebsite.service.AdImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdImageServiceImpl implements AdImageService {

    @Value("${app.upload-url}")   // 复用视频前缀
    private String uploadUrlPrefix;

    private final AdImageMapper imageMapper;
    public AdImageServiceImpl(AdImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public List<AdvertisementVo> getImagesByPrefix(String prefix, int limit) {
        List<AdImage> list = imageMapper.findByImagePrefix(prefix);
        if (list.isEmpty()) return Collections.emptyList();
        Collections.shuffle(list);
        return list.stream()
                .limit(limit)
                .map(this::toVo)   // 统一转 VO
                .collect(Collectors.toList());
    }

    @Override
    public void recordView(Integer id)  { imageMapper.incrementViews(id); }
    @Override
    public void recordClick(Integer id) { imageMapper.incrementClicks(id); }

    // 把 AdImage → AdvertisementVo（复用你已有 VO）
    private AdvertisementVo toVo(AdImage img) {
        AdvertisementVo vo = new AdvertisementVo();
        vo.setId(img.getId());
        vo.setAdType("image");
        vo.setTitle(img.getTitle());
        vo.setDescription(img.getDescription());
        vo.setDuration(0);              // 图片无时长
        vo.setViews(img.getViews());
        vo.setClicks(img.getClicks());
        vo.setIsActive(img.getIsActive());
        vo.setVideoFullUrl(uploadUrlPrefix + img.getImageUrl());
        return vo;
    }
}