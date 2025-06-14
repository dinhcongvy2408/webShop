package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.Repository.ProductRepository;
import com.example.CallApiAngular.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Path fileStorageLocation = Paths.get("uploads/images");
    private final String imageBaseUrl = "http://localhost:8080/images";

    public ProductService() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ các tập tin đã tải lên.", ex);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            if (product.getImages() != null && !product.getImages().startsWith("http")) {
                product.setImages(imageBaseUrl + "/" + product.getImages());
                product.setImagePath(imageBaseUrl + "/" + product.getImagePath());
            }
        });
        return products;
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && product.getImages() != null && !product.getImages().startsWith("http")) {
            product.setImages(imageBaseUrl + "/" + product.getImages());
            product.setImagePath(imageBaseUrl + "/" + product.getImagePath());
        }
        return product;
    }

    @Transactional
    public Product createProduct(Product product, MultipartFile imageFile) {
        try {
            if (product.getGroup() == null || product.getGroup().getId() == null) {
                throw new RuntimeException("Group là bắt buộc");
            }

            if (imageFile == null || imageFile.isEmpty()) {
                throw new RuntimeException("Tệp hình ảnh là bắt buộc");
            }

            String fileName = saveMultipartFile(imageFile);

            Product newProduct = new Product();
            newProduct.setName(product.getName());
            newProduct.setPrice(product.getPrice());
            newProduct.setImages(fileName);
            newProduct.setGroup(product.getGroup());
            newProduct.setImagePath(product.getImages());

            Product savedProduct = productRepository.save(newProduct);
            String imageUrl = "http://localhost:8080/images/" + fileName;
            savedProduct.setImagePath(imageUrl);
            return savedProduct;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo sản phẩm: " + e.getMessage());
        }
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isPresent()) {
            try {
                Product updated = existing.get();
                updated.setName(product.getName());
                updated.setPrice(product.getPrice());
                if (product.getImages() != null) {
                    updated.setImages(product.getImages());
                    updated.setImagePath(product.getImages());
                }
                updated.setGroup(product.getGroup());
                Product savedProduct = productRepository.save(updated);
                if (savedProduct.getImages() != null) {
                    savedProduct.setImages(imageBaseUrl + "/" + savedProduct.getImages());
                    savedProduct.setImagePath(imageBaseUrl + "/" + savedProduct.getImagePath());
                }
                return savedProduct;
            } catch (Exception e) {
                throw new RuntimeException("Error updating product: " + e.getMessage());
            }
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public String saveMultipartFile(MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu trữ tệp. Vui lòng thử lại!", ex);
        }
    }

    public List<Product> getProductsByGroupId(Long groupId) {
        List<Product> products = productRepository.findByGroupId(groupId);
        products.forEach(product -> {
            if (product.getImages() != null && !product.getImages().startsWith("http")) {
                product.setImages(imageBaseUrl + "/" + product.getImages());
                product.setImagePath(imageBaseUrl + "/" + product.getImagePath());
            }
        });
        return products;
    }

        
    public Page<Product> getAllProductsPaginated(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);// Lấy danh sách sản phẩm từ database
        productPage.getContent().forEach(product -> {
            if (product.getImages() != null && !product.getImages().startsWith("http")) {
                product.setImages(imageBaseUrl + "/" + product.getImages());
            }
        });
        return productPage;
    }
}
