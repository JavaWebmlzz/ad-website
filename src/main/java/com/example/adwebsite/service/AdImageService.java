package com.example.adwebsite.service;

import com.example.adwebsite.dto.AdvertisementVo;

import java.util.List;

public interface AdImageService {
    List<AdvertisementVo> getImagesByPrefix(String prefix, int limit);
    void recordView(Integer id);
    void recordClick(Integer id);
}