package com.habeshabite.backend.repository;

import com.habeshabite.backend.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategoryIgnoreCase(String category);
    List<Food> findByIsAvailableTrue();
}
