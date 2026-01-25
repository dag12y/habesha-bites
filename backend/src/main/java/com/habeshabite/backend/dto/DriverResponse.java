package com.habeshabite.backend.dto;

import com.habeshabite.backend.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Driver.DriverStatus status;
    private Boolean isActive;
    private Long currentOrderId;
    private String deliveryAddress;
    private String customerPhone;
}
