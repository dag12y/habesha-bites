package com.habeshabite.backend.repository;

import com.habeshabite.backend.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    Optional<Table> findByTableNumber(Integer tableNumber);
    List<Table> findByIsActiveTrue();
}
