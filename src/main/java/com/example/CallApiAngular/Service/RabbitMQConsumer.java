package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.DTO.CartMessage;
import com.example.CallApiAngular.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @RabbitListener(queues = RabbitMQConfig.CART_QUEUE)
    public void receiveCartMessage(CartMessage cartMessage) {
        try {
            logger.info("Nhận message từ queue: {}", cartMessage);

            // Xử lý message
            orderService.processCartMessage(cartMessage);

            logger.info("Xử lý message thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý message: {}", e.getMessage());
        }
    }
}