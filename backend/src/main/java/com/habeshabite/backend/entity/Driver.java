package com.habeshabite.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum DriverStatus {
        AVAILABLE, OCCUPIED, OFFLINE
    }
}
