package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.DTO.CartMessage;
import com.example.CallApiAngular.config.RabbitMQConfig;
import com.example.CallApiAngular.entity.Order;
import com.example.CallApiAngular.enums.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartMessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(CartMessageConsumer.class);

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.CART_QUEUE)
    public void processCartMessage(CartMessage message) {
        try {
            logger.info("Bắt đầu xử lý message cho user: {}", message.getUserId());

            // Cập nhật trạng thái đơn hàng
            Order order = orderService.getLatestOrderByUserId(message.getUserId());
            if (order != null) {
                order.setStatus(OrderStatus.PROCESSING);
                orderService.saveOrder(order);
                logger.info("Đã cập nhật trạng thái đơn hàng {} thành PROCESSING", order.getId());
            }
            logger.info("Đã xử lý message thành công cho user: {}", message.getUserId());
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý message cho user {}: {}", message.getUserId(), e.getMessage());
            throw e;
        }
    }
}