package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.AuthResponse;
import com.habeshabite.backend.dto.LoginRequest;
import com.habeshabite.backend.dto.RegisterRequest;
import com.habeshabite.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        String message = authService.register(request);
        boolean success = message.equals("User registered successfully");
        return new AuthResponse(success, message);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String message = authService.login(request.getEmail(), request.getPassword());
        boolean success = message.equals("Login successful");
        return new AuthResponse(success, message);
    }
}
