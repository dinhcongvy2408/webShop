package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.DTO.Request.ApiResponse;
import com.example.CallApiAngular.DTO.Response.CartItemResponse;
import com.example.CallApiAngular.Service.CartService;
import com.example.CallApiAngular.entity.CartItem;
import com.example.CallApiAngular.entity.Product;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.Service.ProductService;
import com.example.CallApiAngular.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService; // Cần thiết để lấy thông tin product cho response

    // DTO để nhận request thêm/cập nhật giỏ hàng từ frontend
    static class CartOperationRequest {
        public Long productId;
        public int quantity;
    }

    private CartItemResponse convertCartItemToResponse(CartItem item) {
        Product product = item.getProduct();
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(product.getId());
        response.setQuantity(item.getQuantity());
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());
        response.setImages(product.getImages()); // Giả định Product có trường images
        return response;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems(@PathVariable Long userId) {
        log.info("Getting cart items for userId: {}", userId);
        List<CartItem> cartItems = cartService.getCartItems(userId);
        List<CartItemResponse> responseList = cartItems.stream()
                .map(this::convertCartItemToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<CartItemResponse>>builder()
                .result(responseList)
                .build());
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> addProductToCart(@PathVariable Long userId,
            @RequestBody CartOperationRequest request) {
        log.info("Adding product {} with quantity {} to cart for userId: {}", request.productId, request.quantity,
                userId);
        CartItem addedItem = cartService.addProductToCart(userId, request.productId, request.quantity);
        return ResponseEntity.ok(ApiResponse.<CartItemResponse>builder()
                .result(convertCartItemToResponse(addedItem))
                .build());
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateProductQuantity(@PathVariable Long userId,
            @RequestBody CartOperationRequest request) {
        log.info("Updating product {} quantity to {} in cart for userId: {}", request.productId, request.quantity,
                userId);
        CartItem updatedItem = cartService.updateProductQuantity(userId, request.productId, request.quantity);
        // Nếu quantity <= 0, item sẽ bị xóa và updateProductQuantity trả về null
        if (updatedItem == null) {
            return ResponseEntity
                    .ok(ApiResponse.<CartItemResponse>builder().message("Product removed from cart").build());
        }
        return ResponseEntity.ok(ApiResponse.<CartItemResponse>builder()
                .result(convertCartItemToResponse(updatedItem))
                .build());
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeProductFromCart(@PathVariable Long userId,
            @PathVariable Long productId) {
        log.info("Removing product {} from cart for userId: {}", productId, userId);
        cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().message("Product removed from cart").build());
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        log.info("Clearing cart for userId: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().message("Cart cleared").build());
    }
}
