package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.Service.OrderService;
import com.example.CallApiAngular.Service.UserService;
import com.example.CallApiAngular.entity.Order;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.enums.OrderStatus;
import com.example.CallApiAngular.DTO.OrderRequest;
import com.example.CallApiAngular.DTO.CartMessage;
import com.example.CallApiAngular.Service.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final ThreadLocal<Boolean> isProcessing = new ThreadLocal<>();

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            // Kiểm tra xem có đang trong quá trình xử lý không
            if (isProcessing.get() != null && isProcessing.get()) {
                logger.warn("Phát hiện vòng lặp xử lý order cho user: {}", request.getUserId());
                return ResponseEntity.badRequest().body("Đang trong quá trình xử lý order");
            }

            isProcessing.set(true);
            logger.info("Bắt đầu tạo đơn hàng cho user: {}", request.getUserId());
            logger.debug("Request body: {}", request);

            if (request.getUserId() == null) {
                return ResponseEntity.badRequest().body("UserId không được để trống");
            }

            // Validate shipping info
            if (request.getShippingInfo() == null) {
                return ResponseEntity.badRequest().body("Thông tin giao hàng không được để trống");
            }
            if (request.getShippingInfo().getAddress() == null
                    || request.getShippingInfo().getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Địa chỉ giao hàng không được để trống");
            }
            if (request.getShippingInfo().getPhoneNumber() == null
                    || request.getShippingInfo().getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Số điện thoại không được để trống");
            }

            // Lấy user từ request
            Users user = userService.getUserById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + request.getUserId()));

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Đơn hàng phải có ít nhất một sản phẩm");
            }

            // Lưu order trực tiếp vào database
            Order savedOrder = orderService.createOrderFromRequest(request, user);
            logger.info("Đã lưu order với ID: {}", savedOrder.getId());

            // Gửi chỉ order ID qua RabbitMQ để xử lý bất đồng bộ
            CartMessage cartMessage = new CartMessage();
            cartMessage.setOrderId(savedOrder.getId()); // Gửi order ID
            cartMessage.setUserId(request.getUserId());


            logger.info("Bắt đầu gửi message qua RabbitMQ cho order ID: {}", savedOrder.getId());
            rabbitMQProducer.sendCartMessage(cartMessage);
            logger.info("Đã gửi message qua RabbitMQ thành công cho order ID: {}", savedOrder.getId());

            // Tạo response đơn giản
            OrderResponse response = new OrderResponse();
            response.setId(savedOrder.getId());
            response.setStatus(savedOrder.getStatus());
            response.setTotalAmount(savedOrder.getTotalAmount());
            response.setCreatedAt(savedOrder.getCreatedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo đơn hàng: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            isProcessing.remove();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
        try {
            Users user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
            List<Order> orders = orderService.getUserOrders(user);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
        try {
            OrderStatus status = orderService.getOrderStatus(orderId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<?> getLatestOrder(@PathVariable Long userId) {
        try {
            Users user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
            Order latestOrder = orderService.getLatestOrder(user);
            return ResponseEntity.ok(latestOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Inner class để tránh vòng lặp JSON
    private static class OrderResponse {
        private Long id;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private java.time.LocalDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}