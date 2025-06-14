package com.example.CallApiAngular.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "shipping_info")
public class ShippingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String note;

    @OneToOne(mappedBy = "shippingInfo")
    private Order order;
}