package com.habeshabite.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String category; // e.g., Pizza, Drinks, Desserts

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_available")
    private Boolean isAvailable = true;
}
