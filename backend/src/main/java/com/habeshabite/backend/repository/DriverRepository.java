package com.habeshabite.backend.repository;

import com.habeshabite.backend.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByIsActiveTrue();
    List<Driver> findByIsActiveTrueAndStatus(Driver.DriverStatus status);
    Optional<Driver> findByEmail(String email);
}
