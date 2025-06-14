package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.Service.ImageService;
import com.example.CallApiAngular.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:4200")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/{src}")
    public ResponseEntity<Image> getImageBySrc(@PathVariable String src) {
        Image image = imageService.getImageBySrc(src);
        return image != null ? ResponseEntity.ok(image) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Image> createImage(@RequestBody Image image) {
        return ResponseEntity.ok(imageService.createImage(image));
    }

    @PutMapping
    public ResponseEntity<Image> updateImage(@RequestParam String src, @RequestBody Image image) {
        Image updated = imageService.updateImage(src, image);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam String src) {
        imageService.deleteImage(src);
        return ResponseEntity.noContent().build();
    }
}
