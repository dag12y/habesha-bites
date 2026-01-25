package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.DriverRequest;
import com.habeshabite.backend.dto.DriverResponse;
import com.habeshabite.backend.entity.Driver;
import com.habeshabite.backend.entity.Order;
import com.habeshabite.backend.repository.DriverRepository;
import com.habeshabite.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;

    public DriverService(DriverRepository driverRepository, OrderRepository orderRepository) {
        this.driverRepository = driverRepository;
        this.orderRepository = orderRepository;
    }

    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DriverResponse> getAvailableDrivers() {
        return driverRepository.findByIsActiveTrueAndStatus(Driver.DriverStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DriverResponse createDriver(DriverRequest request) {
        if (driverRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("A driver with this email already exists");
        }
        Driver driver = new Driver();
        driver.setFullName(request.getFullName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        driver.setIsActive(true);
        return mapToResponse(driverRepository.save(driver));
    }

    public DriverResponse updateDriver(Long id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        if (!request.getEmail().equals(driver.getEmail())
                && driverRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("A driver with this email already exists");
        }
        driver.setFullName(request.getFullName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        return mapToResponse(driverRepository.save(driver));
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        if (driver.getStatus() == Driver.DriverStatus.OCCUPIED) {
            throw new RuntimeException("Cannot delete driver who is currently on a delivery. Unassign the order first.");
        }
        driver.setIsActive(false);
        driverRepository.save(driver);
    }

    private DriverResponse mapToResponse(Driver driver) {
        DriverResponse r = new DriverResponse();
        r.setId(driver.getId());
        r.setFullName(driver.getFullName());
        r.setEmail(driver.getEmail());
        r.setPhone(driver.getPhone());
        r.setStatus(driver.getStatus());
        r.setIsActive(driver.getIsActive());
        r.setCurrentOrderId(null);
        r.setDeliveryAddress(null);
        r.setCustomerPhone(null);
        if (driver.getStatus() == Driver.DriverStatus.OCCUPIED) {
            List<Order> active = orderRepository.findByDriverAndStatusNotIn(
                    driver, Arrays.asList(Order.OrderStatus.DELIVERED, Order.OrderStatus.CANCELLED));
            if (!active.isEmpty()) {
                Order o = active.get(0);
                r.setCurrentOrderId(o.getId());
                r.setDeliveryAddress(o.getDeliveryAddress());
                r.setCustomerPhone(o.getPhoneNumber());
            }
        }
        return r;
    }
}
