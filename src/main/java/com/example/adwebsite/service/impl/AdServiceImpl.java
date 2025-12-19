// AdServiceImpl.java
package com.example.adwebsite.service.impl;

import com.example.adwebsite.entity.Advertisement;
import com.example.adwebsite.mapper.AdMapper;
import com.example.adwebsite.service.AdService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class AdServiceImpl implements AdService {

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

    @Override
    public void recordView(Integer id) {
        adMapper.incrementViews(id);
    }

    @Override
    public void recordClick(Integer id) {
        adMapper.incrementClicks(id);
    }
}