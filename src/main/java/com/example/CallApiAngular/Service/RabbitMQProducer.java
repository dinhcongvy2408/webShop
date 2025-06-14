package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.DTO.CartMessage;
import com.example.CallApiAngular.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;

@Service
public class RabbitMQProducer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCartMessage(CartMessage cartMessage) {
        try {
            logger.info("Bắt đầu gửi message cho user: {}", cartMessage.getUserId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CART_EXCHANGE,
                    RabbitMQConfig.CART_ROUTING_KEY,
                    cartMessage,
                    message -> {
                        message.getMessageProperties().setContentType("application/json");
                        return message;
                    });

            logger.info("Đã gửi message thành công cho user: {}", cartMessage.getUserId());
        } catch (AmqpException e) {
            logger.error("Lỗi khi gửi message cho user {}: {}", cartMessage.getUserId(), e.getMessage());
            throw new RuntimeException("Không thể gửi message đến RabbitMQ: " + e.getMessage());
        }
    }
}