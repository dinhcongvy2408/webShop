package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.Repository.CartItemRepository;
import com.example.CallApiAngular.Repository.CartRepository;
import com.example.CallApiAngular.Repository.UserRepository;
import com.example.CallApiAngular.entity.Cart;
import com.example.CallApiAngular.entity.CartItem;
import com.example.CallApiAngular.entity.Product;
import com.example.CallApiAngular.entity.Users;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            return cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            return cartItemRepository.save(newCartItem);
        }
    }

    @Transactional
    public CartItem updateProductQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem != null) {
            if (quantity <= 0) {
                cartItemRepository.delete(existingCartItem);
                return null; // Return null to indicate item was removed
            } else {
                existingCartItem.setQuantity(quantity);
                return cartItemRepository.save(existingCartItem);
            }
        } else {
            throw new RuntimeException("Product not in cart for user: " + userId + ", Product ID: " + productId);
        }
    }

    @Transactional
    public void removeProductFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        CartItem existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem != null) {
            cartItemRepository.delete(existingCartItem);
        } else {
            throw new RuntimeException("Product not in cart for user: " + userId + ", Product ID: " + productId);
        }
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart(cart);
    }
}
