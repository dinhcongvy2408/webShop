package com.example.CallApiAngular.DTO.Response;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private double productPrice;
    private Integer quantity;
    private String images;
}