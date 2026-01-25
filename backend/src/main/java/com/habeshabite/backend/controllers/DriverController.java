package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.DriverRequest;
import com.habeshabite.backend.dto.DriverResponse;
import com.habeshabite.backend.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @PostMapping
    public ResponseEntity<?> createDriver(@RequestBody DriverRequest request) {
        try {
            return ResponseEntity.ok(driverService.createDriver(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new com.habeshabite.backend.dto.ErrorResponse("Error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverRequest request) {
        try {
            return ResponseEntity.ok(driverService.updateDriver(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new com.habeshabite.backend.dto.ErrorResponse("Error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        try {
            driverService.deleteDriver(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new com.habeshabite.backend.dto.ErrorResponse("Error", e.getMessage()));
        }
    }
}
