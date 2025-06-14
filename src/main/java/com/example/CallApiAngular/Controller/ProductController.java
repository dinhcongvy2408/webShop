package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.Service.ProductService;
import com.example.CallApiAngular.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(value = "groupId", required = false) Long groupId) {
        if (groupId != null) {
            return ResponseEntity.ok(productService.getProductsByGroupId(groupId));
        } else {
            return ResponseEntity.ok(productService.getAllProducts());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProductWithImage(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);

            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên sản phẩm không được để trống");
            }
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Giá sản phẩm phải lớn hơn 0");
            }
            if (product.getGroup() == null || product.getGroup().getId() == null) {
                return ResponseEntity.badRequest().body("Nhóm sản phẩm không được để trống");
            }
            if (image != null && !image.isEmpty()) {
                String fileName = productService.saveMultipartFile(image);
                String imageUrl = "http://localhost:8080/images/" + fileName;
                product.setImages(imageUrl);
                product.setImagePath(imageUrl);
            }

            Product savedProduct = productService.createProduct(product, image);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo sản phẩm: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Product>> getProductsPaginated(
            @PageableDefault( sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Product> productPage = productService.getAllProductsPaginated(pageable);
        return ResponseEntity.ok(productPage);
    }
}
