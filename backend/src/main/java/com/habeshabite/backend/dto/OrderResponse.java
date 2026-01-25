package com.habeshabite.backend.dto;

import com.habeshabite.backend.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private Order.OrderStatus status;
    private String deliveryAddress;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private String driverPhone;
}

