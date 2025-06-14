package com.example.CallApiAngular.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

@Configuration
public class RabbitMQConfig {

    public static final String CART_QUEUE = "cart.queue";
    public static final String CART_EXCHANGE = "cart.exchange";
    public static final String CART_ROUTING_KEY = "cart.routingkey";

    @Bean
    public Queue cartQueue() {
        return new Queue(CART_QUEUE, true); // durable = true
    }

    @Bean
    public DirectExchange cartExchange() {
        return new DirectExchange(CART_EXCHANGE);
    }

    @Bean
    public Binding cartBinding(Queue cartQueue, DirectExchange cartExchange) {
        return BindingBuilder
                .bind(cartQueue)
                .to(cartExchange)
                .with(CART_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //gửi message đến RabbitMQ
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());//chuyển đổi message sang JSON
        //xác nhận thành công, thất bại
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("Message không được gửi thành công: " + cause);
            }
        });
        return rabbitTemplate;
    }

    //nhận và xử lý message từ queue
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());//chuyển đổi JSON thành Java object
        factory.setConcurrentConsumers(3);//3 message có thể được xử lý song song
        factory.setMaxConcurrentConsumers(5);//tăng số thread lên tối đa 5
        factory.setPrefetchCount(1);// consumer sẽ chỉ nhận 1 message tại một thời điểm
        return factory;
    }
}