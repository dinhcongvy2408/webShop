package com.example.CallApiAngular.Repository;

import com.example.CallApiAngular.entity.Cart;
import com.example.CallApiAngular.entity.Product;
import com.example.CallApiAngular.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
}