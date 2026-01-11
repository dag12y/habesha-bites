package com.habeshabite.backend.dto;

import lombok.Data;

@Data
public class FoodRequest {
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable = true;
}

