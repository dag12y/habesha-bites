package com.habeshabite.backend.controllers;

import com.habeshabite.backend.dto.AuthResponse;
import com.habeshabite.backend.dto.LoginRequest;
import com.habeshabite.backend.dto.RegisterRequest;
import com.habeshabite.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow requests from your React frontend
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        String token = authService.registerWithToken(request);
        if (token != null) {
            return new AuthResponse(true, "User registered successfully", token);
        } else {
            // If token is null, check what went wrong
            String message = authService.register(request);
            boolean success = message.equals("User registered successfully");
            return new AuthResponse(success, message);
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String token = authService.loginWithToken(request.getEmail(), request.getPassword());
        if (token != null) {
            return new AuthResponse(true, "Login successful", token);
        } else {
            return new AuthResponse(false, "Invalid credentials");
        }
    }

}
