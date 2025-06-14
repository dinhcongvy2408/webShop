package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.Service.BannerService;
import com.example.CallApiAngular.entity.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "http://localhost:4200")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ResponseEntity<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getBannerById(id);
        return banner != null ? ResponseEntity.ok(banner) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBanner(@RequestBody Banner banner) {
        try {
            if (banner == null) {
                return ResponseEntity.badRequest().body("Banner data is required");
            }

            if (banner.getSrc() == null || banner.getSrc().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Banner source is required");
            }

            if (banner.getHref() == null || banner.getHref().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Banner href is required");
            }

            if (banner.getGroup() == null || banner.getGroup().getId() == null) {
                return ResponseEntity.badRequest().body("Banner group is required");
            }

            Banner createdBanner = bannerService.createBanner(banner);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBanner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating banner: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        try {
            Banner updatedBanner = bannerService.updateBanner(id, banner);
            return updatedBanner != null ? ResponseEntity.ok(updatedBanner) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating banner: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long id) {
        try {
            bannerService.deleteBanner(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting banner: " + e.getMessage());
        }
    }
}
