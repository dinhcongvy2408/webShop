package com.example.CallApiAngular.DTO;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<OrderItemRequest> items;

    @NotNull(message = "Tổng tiền không được để trống")
    @Min(value = 0, message = "Tổng tiền phải lớn hơn hoặc bằng 0")
    private Double totalAmount;

    private ShippingInfoRequest shippingInfo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID không được để trống")
        private Long productId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    @Data
    public static class ShippingInfoRequest {
        private String address;
        private String phoneNumber;
        private String note;
    }
}