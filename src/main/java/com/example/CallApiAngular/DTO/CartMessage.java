package com.example.CallApiAngular.DTO;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartMessage {
    private Long orderId;
    private Long userId;
    private List<CartItemMessage> items;
    private BigDecimal totalAmount;
    private ShippingInfoMessage shippingInfo;

    @Data
    public static class CartItemMessage {
        private Long productId;
        private Integer quantity;
    }

    @Data
    public static class ShippingInfoMessage {
        private String address;
        private String phoneNumber;
        private String note;
    }
}