// AdService.java
package com.example.adwebsite.service;

import com.example.adwebsite.dto.AdvertisementVo;
import com.example.adwebsite.entity.Advertisement;
import java.util.List;

public interface AdService {
    List<Advertisement> getAllAds();
    Advertisement getAdById(Integer id);
    Advertisement createAd(Advertisement ad);
    Advertisement updateAd(Integer id, Advertisement ad);
    void deleteAd(Integer id);
    Advertisement getRandomAd();
    void recordView(Integer id);
    void recordClick(Integer id);

    AdvertisementVo getAdVoById(Integer id);

    AdvertisementVo getRandomAdVo();

    List<AdvertisementVo> getAdsByVideoPrefix(String prefix, int limit);
}