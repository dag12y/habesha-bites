package com.habeshabite.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {
    private String fullName;
    private String email;
    private String phone;
}
