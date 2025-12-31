package com.habeshabite.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    private String phoneNumber;
}

