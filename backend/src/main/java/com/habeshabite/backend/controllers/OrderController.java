package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.OrderRequest;
import com.habeshabite.backend.dto.OrderResponse;
import com.habeshabite.backend.dto.StatusUpdateRequest;
import com.habeshabite.backend.entity.Order;
import com.habeshabite.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    // POST /api/orders - Create new order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        try {
            String userEmail = getCurrentUserEmail();
            OrderResponse order = orderService.createOrder(userEmail, request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/orders/my-orders - Get current user's orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        try {
            String userEmail = getCurrentUserEmail();
            List<OrderResponse> orders = orderService.getUserOrders(userEmail);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/orders/{id} - Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        try {
            String userEmail = getCurrentUserEmail();
            OrderResponse order = orderService.getOrderById(id, userEmail);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/orders - Get all orders (ADMIN only)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/orders/status/{status} - Get orders by status (ADMIN only)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/orders/{id}/status - Update order status (ADMIN only)
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            OrderResponse order = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/orders/{id}/cancel - Cancel order
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        try {
            String userEmail = getCurrentUserEmail();
            if (orderService.cancelOrder(id, userEmail)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

