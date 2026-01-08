package com.habeshabite.backend.service;

import com.habeshabite.backend.dto.UserResponse;
import com.habeshabite.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .orElse(null);
    }

    public UserResponse getUserById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
                .map(user -> {
                    Long userId = user.getId();
                    if (userId == null) {
                        userId = 0L;
                    }
                    return new UserResponse(
                            userId,
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getRole(),
                            user.getCreatedAt()
                    );
                })
                .orElse(null);
    }
}

