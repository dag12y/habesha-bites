package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.RegisterRequest;
import com.habeshabite.backend.entity.User;
import com.habeshabite.backend.repository.UserRepository;
import com.habeshabite.backend.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "User already exists";
        }

        // Validate required fields
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return "Phone number is required";
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // default role

        userRepository.save(user);
        return "User registered successfully";
    }

    public String registerWithToken(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return null; // User already exists
        }

        // Validate required fields
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return null; // Phone number is required
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // default role

        userRepository.save(user);
        // Generate token after successful registration
        return jwtUtil.generateToken(user);
    }

    public String login(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(password, user.getPassword())
                        ? "Login successful"
                        : "Invalid credentials")
                .orElse("User not found");
    }

    public String loginWithToken(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return jwtUtil.generateToken(user); // pass the User object
                    }
                    return null;
                }).orElse(null);
    }

}

