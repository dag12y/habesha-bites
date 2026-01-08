package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.FoodRequest;
import com.habeshabite.backend.dto.FoodResponse;
import com.habeshabite.backend.entity.Food;
import com.habeshabite.backend.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodResponse> getFoodsByCategory(String category) {
        return foodRepository.findByCategoryIgnoreCase(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodResponse> getAvailableFoods() {
        return foodRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FoodResponse getFoodById(Long id) {
        if (id == null) {
            return null;
        }
        return foodRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    public FoodResponse createFood(FoodRequest request) {
        Food food = new Food();
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setCategory(request.getCategory());
        food.setImageUrl(request.getImageUrl());
        food.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        
        Food saved = foodRepository.save(food);
        return mapToResponse(saved);
    }

    public FoodResponse updateFood(Long id, FoodRequest request) {
        if (id == null) {
            return null;
        }
        return foodRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    existing.setPrice(request.getPrice());
                    existing.setCategory(request.getCategory());
                    existing.setImageUrl(request.getImageUrl());
                    if (request.getIsAvailable() != null) {
                        existing.setIsAvailable(request.getIsAvailable());
                    }
                    Food saved = foodRepository.save(existing);
                    return mapToResponse(saved);
                })
                .orElse(null);
    }

    public boolean deleteFood(Long id) {
        if (id == null) {
            return false;
        }
        return foodRepository.findById(id)
                .map(food -> {
                    if (food != null) {
                        foodRepository.delete(food);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    private FoodResponse mapToResponse(Food food) {
        Objects.requireNonNull(food, "Food cannot be null");
        Long foodId = food.getId();
        if (foodId == null) {
            foodId = 0L;
        }
        return new FoodResponse(
                foodId,
                food.getName(),
                food.getDescription(),
                food.getPrice(),
                food.getCategory(),
                food.getImageUrl(),
                food.getIsAvailable()
        );
    }
}

