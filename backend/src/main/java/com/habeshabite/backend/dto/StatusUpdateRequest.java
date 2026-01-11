package com.habeshabite.backend.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.habeshabite.backend.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatusUpdateRequest {
    private Order.OrderStatus status;

    @JsonSetter("status")
    public void setStatusFromString(String statusString) {
        if (statusString != null && !statusString.isEmpty()) {
            try {
                this.status = Order.OrderStatus.valueOf(statusString.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + statusString + 
                    ". Valid values are: PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED");
            }
        }
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
}

