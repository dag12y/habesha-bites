package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    // Dummy "database"
    private final Map<String, String> users = new HashMap<>();

    public String register(RegisterRequest request) {
        if (users.containsKey(request.getEmail())) {
            return "User already exists";
        }

        users.put(request.getEmail(), request.getPassword());
        return "User registered successfully";
    }

    public String login(String email, String password) {
        if (!users.containsKey(email)) {
            return "User not found";
        }

        if (!users.get(email).equals(password)) {
            return "Invalid credentials";
        }

        return "Login successful";
    }
}
