package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.Repository.ImageRepository;
import com.example.CallApiAngular.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Image getImageBySrc(String src) {
        return imageRepository.findById(src).orElse(null);
    }

    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    public Image updateImage(String src, Image image) {
        Image existing = imageRepository.findById(src).orElse(null);
        if (existing != null) {
            existing.sethref(image.gethref());
            existing.setalt(image.getalt());
            return imageRepository.save(existing);
        } else {
            return null;
        }
    }

    public void deleteImage(String src) {
        imageRepository.deleteById(src);
    }
}
