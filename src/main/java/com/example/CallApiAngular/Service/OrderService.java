package com.example.CallApiAngular.Service;

import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Async;
import com.example.CallApiAngular.Repository.OrderRepository;
import com.example.CallApiAngular.entity.Product;
import com.example.CallApiAngular.Service.CartService;
import com.example.CallApiAngular.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.CallApiAngular.entity.Order;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.DTO.OrderRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import com.example.CallApiAngular.exception.OrderNotFoundException;
import com.example.CallApiAngular.exception.InsufficientStockException;
import com.example.CallApiAngular.exception.OrderProcessingException;
import java.math.BigDecimal;
import com.example.CallApiAngular.entity.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.CallApiAngular.DTO.CartMessage;
import com.example.CallApiAngular.entity.ShippingInfo;
import com.example.CallApiAngular.entity.OrderItem;
import com.example.CallApiAngular.Repository.OrderItemRepository;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Order createOrderFromRequest(OrderRequest request, Users user) {
        logger.info("Bắt đầu tạo đơn hàng cho user: {}", user.getId());

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng không hợp lệ: không có sản phẩm nào");
        }

        if (user == null || user.getId() == null) {
            logger.error("User không hợp lệ: {}", user);
            throw new RuntimeException("User không hợp lệ");
        }

        // Validate shipping info
        if (request.getShippingInfo() == null) {
            throw new RuntimeException("Thông tin giao hàng không được để trống");
        }
        if (request.getShippingInfo().getAddress() == null || request.getShippingInfo().getAddress().trim().isEmpty()) {
            throw new RuntimeException("Địa chỉ giao hàng không được để trống");
        }
        if (request.getShippingInfo().getPhoneNumber() == null
                || request.getShippingInfo().getPhoneNumber().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }

        // Lấy hoặc tạo cart cho user trước
        Cart userCart = cartService.getOrCreateCart(user.getId());
        logger.info("Đã lấy/tao cart cho user: {}", user.getId());

        // Tạo shipping info
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setAddress(request.getShippingInfo().getAddress());
        shippingInfo.setPhoneNumber(request.getShippingInfo().getPhoneNumber());
        shippingInfo.setNote(request.getShippingInfo().getNote());

        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(request.getTotalAmount()));
        order.setShippingInfo(shippingInfo);

        // Lưu order trước để có ID
        Order savedOrder = orderRepository.save(order);
        logger.info("Đã tạo order với ID: {}", savedOrder.getId());

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            logger.info("Xử lý item với productId: {}", itemRequest.getProductId());

            if (itemRequest.getProductId() == null) {
                logger.error("Product ID là null");
                throw new RuntimeException("Product ID không được để trống");
            }

            Product product = productService.getProductById(itemRequest.getProductId());
            logger.info("Tìm thấy product: {}", product != null ? product.getId() : "null");

            if (product == null) {
                throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + itemRequest.getProductId());
            }

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ cho sản phẩm: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setPrice(BigDecimal.valueOf(product.getPrice()));
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubtotal(
                    BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            orderItems.add(orderItem);
            logger.info("Đã thêm item vào order: productId={}, quantity={}", product.getId(),
                    itemRequest.getQuantity());
        }

        savedOrder.setOrderItems(orderItems);
        return orderRepository.save(savedOrder);
    }

    @Transactional
    public void processCartMessage(CartMessage cartMessage) {
        try {
            logger.info("Bắt đầu xử lý cart message cho order ID: {}", cartMessage.getOrderId());

            if (cartMessage.getOrderId() == null) {
                logger.error("Order ID là null trong cart message");
                return; // Bỏ qua message nếu không có Order ID
            }

            Order order = orderRepository.findById(cartMessage.getOrderId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy order với ID: " + cartMessage.getOrderId()));

            logger.info("Đã lấy order với ID: {} cho user: {}", order.getId(), order.getUser().getId());

            processOrder(order);

            logger.info("Đã hoàn thành xử lý message cho order ID: {}", cartMessage.getOrderId());
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý cart message cho order ID {}: {}", cartMessage.getOrderId(), e.getMessage());
            throw new RuntimeException("Lỗi khi xử lý đơn hàng: " + e.getMessage());
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Order> processOrder(Order orderInput) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Bắt đầu xử lý logic order bất đồng bộ cho order ID: {}", orderInput.getId());

                Order order = orderRepository.findById(orderInput.getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Không tìm thấy order với ID: " + orderInput.getId() + " trong xử lý bất đồng bộ"));

                for (OrderItem item : order.getOrderItems()) {
                    Product product = productService.getProductById(item.getProduct().getId());
                    if (product.getStock() < item.getQuantity()) {
                        throw new InsufficientStockException("Sản phẩm " + product.getName() + " không đủ số lượng");
                    }
                }

                for (OrderItem item : order.getOrderItems()) {
                    Product product = productService.getProductById(item.getProduct().getId());
                    product.setStock(product.getStock() - item.getQuantity());
                    productService.updateProduct(product.getId(), product);
                }

                order.setStatus(OrderStatus.PROCESSING);
                Order savedOrder = orderRepository.save(order);

                cartService.clearCart(order.getUser().getId());

                savedOrder.setStatus(OrderStatus.COMPLETED);
                Order completedOrder = orderRepository.save(savedOrder);

                logger.info("Đã hoàn thành xử lý logic order bất đồng bộ cho order ID: {}", completedOrder.getId());
                return completedOrder;
            } catch (Exception e) {
                logger.error("Lỗi xử lý logic order bất đồng bộ cho order ID {}: {}", orderInput.getId(),
                        e.getMessage(), e);

                Order orderToUpdate;
                try {
                    orderToUpdate = orderRepository.findById(orderInput.getId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy order với ID: " + orderInput.getId()
                                    + " khi cập nhật trạng thái FAILED"));
                } catch (Exception fetchException) {
                    logger.error("Không thể tải lại order ID {} để cập nhật trạng thái FAILED: {}", orderInput.getId(),
                            fetchException.getMessage());
                    throw new OrderProcessingException("Lỗi xử lý đơn hàng và không thể cập nhật trạng thái FAILED", e);
                }

                orderToUpdate.setStatus(OrderStatus.FAILED);
                orderRepository.save(orderToUpdate);
                throw new OrderProcessingException("Lỗi xử lý đơn hàng", e);
            }
        });
    }

    public OrderStatus getOrderStatus(Long orderId) {
        logger.info("Kiểm tra trạng thái đơn hàng ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy đơn hàng với ID: {}", orderId);
                    return new RuntimeException("Không tìm thấy đơn hàng");
                });
        logger.info("Trạng thái đơn hàng {}: {}", orderId, order.getStatus());
        return order.getStatus();
    }

    public List<Order> getUserOrders(Users user) {
        return orderRepository.findByUser(user);
    }

    public Order getLatestOrder(Users user) {
        return orderRepository.findTopByUserOrderByCreatedAtDesc(user);
    }

    public Order getLatestOrderByUserId(Long userId) {
        return orderRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
