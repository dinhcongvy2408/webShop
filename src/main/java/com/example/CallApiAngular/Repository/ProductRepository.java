package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    java.util.List<Product> findByGroupId(Long groupId);
}
