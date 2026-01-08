package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.OrderItemRequest;
import com.habeshabite.backend.dto.OrderItemResponse;
import com.habeshabite.backend.dto.OrderRequest;
import com.habeshabite.backend.dto.OrderResponse;
import com.habeshabite.backend.entity.*;
import com.habeshabite.backend.exception.OrderItem;
import com.habeshabite.backend.repository.FoodRepository;
import com.habeshabite.backend.repository.OrderRepository;
import com.habeshabite.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, FoodRepository foodRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }

    @Transactional
    public OrderResponse createOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("At least one item is required to place an order");
        }

        if (!StringUtils.hasText(request.getDeliveryAddress())) {
            throw new RuntimeException("Delivery address is required");
        }

        String phoneFromRequest = request.getPhoneNumber();
        String phoneToUse = StringUtils.hasText(phoneFromRequest)
                ? phoneFromRequest.trim()
                : user.getPhone();

        if (!StringUtils.hasText(phoneToUse)) {
            throw new RuntimeException("Phone number is required");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setPhoneNumber(phoneToUse);
        order.setStatus(Order.OrderStatus.PENDING);

        double totalAmount = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Long foodId = itemRequest.getFoodId();
            if (foodId == null) {
                throw new RuntimeException("Food ID cannot be null");
            }
            Food food = foodRepository.findById(foodId)
                    .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodId));

            if (!food.getIsAvailable()) {
                throw new RuntimeException("Food item " + food.getName() + " is not available");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFood(food);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(food.getPrice());

            order.getItems().add(orderItem);
            totalAmount += food.getPrice() * itemRequest.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String userEmail) {
        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if order belongs to user or user is admin
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        return mapToResponse(updated);
    }

    @Transactional
    public boolean cancelOrder(Long orderId, String userEmail) {
        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only allow cancellation if order belongs to user or user is admin
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        // Only allow cancellation if order is not already delivered or cancelled
        if (order.getStatus() == Order.OrderStatus.DELIVERED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            return false;
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        return true;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getFood().getId(),
                        item.getFood().getName(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                itemResponses,
                order.getTotalAmount(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getPhoneNumber(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
