package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.FoodRequest;
import com.habeshabite.backend.dto.FoodResponse;
import com.habeshabite.backend.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*") // allow React frontend
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // GET /api/foods
    @GetMapping
    public List<FoodResponse> getAllFoods() {
        return foodService.getAllFoods();
    }

    // GET /api/foods/available
    @GetMapping("/available")
    public List<FoodResponse> getAvailableFoods() {
        return foodService.getAvailableFoods();
    }

    // GET /api/foods/category/{category}
    @GetMapping("/category/{category}")
    public List<FoodResponse> getFoodsByCategory(@PathVariable String category) {
        return foodService.getFoodsByCategory(category);
    }

    // GET /api/foods/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
        FoodResponse food = foodService.getFoodById(id);
        if (food != null) {
            return ResponseEntity.ok(food);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/foods (ADMIN)
    @PostMapping
    public ResponseEntity<FoodResponse> createFood(@RequestBody FoodRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        FoodResponse saved = foodService.createFood(request);
        return ResponseEntity.ok(saved);
    }

    // PUT /api/foods/{id} (ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(@PathVariable Long id, @RequestBody FoodRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        FoodResponse updated = foodService.updateFood(id, request);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/foods/{id} (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        if (foodService.deleteFood(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
