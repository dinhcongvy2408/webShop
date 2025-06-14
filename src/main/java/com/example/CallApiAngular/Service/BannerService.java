package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.entity.Banner;
import com.example.CallApiAngular.Repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id).orElse(null);
    }

    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner updateBanner(Long id, Banner banner) {
        Banner existingBanner = bannerRepository.findById(id).orElse(null);
        if (existingBanner != null) {
            existingBanner.setSrc(banner.getSrc());
            existingBanner.setHref(banner.getHref());
            existingBanner.setGroup(banner.getGroup());
            return bannerRepository.save(existingBanner);
        }
        return null;
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}
