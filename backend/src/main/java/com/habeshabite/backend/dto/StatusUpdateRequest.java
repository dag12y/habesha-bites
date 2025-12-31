package com.habeshabite.backend.dto;

import com.habeshabite.backend.entity.Order;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private Order.OrderStatus status;
}

