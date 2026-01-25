package com.habeshabite.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity; // number of people the table can seat

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // whether the table is available for booking
}
