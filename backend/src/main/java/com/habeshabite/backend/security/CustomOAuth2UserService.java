package com.habeshabite.backend.security;

import com.habeshabite.backend.entity.User;
import com.habeshabite.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = getAttribute(attributes, "email");
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 provider response");
        }

        String fullName = getAttribute(attributes, "name");
        if (!StringUtils.hasText(fullName)) {
            fullName = getAttribute(attributes, "given_name");
        }
        if (!StringUtils.hasText(fullName)) {
            fullName = email;
        }

        final String userEmail = email;
        final String userName = fullName;

        User user = userRepository.findByEmail(userEmail)
                .orElseGet(() -> createUser(userName, userEmail));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());

        return new DefaultOAuth2User(Collections.singleton(authority), attributes, "email");
    }

    private User createUser(String fullName, String email) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(User.Role.USER);
        user.setPhone("PENDING");
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userRepository.save(user);
    }

    @SuppressWarnings("unchecked")
    private String getAttribute(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }
}
