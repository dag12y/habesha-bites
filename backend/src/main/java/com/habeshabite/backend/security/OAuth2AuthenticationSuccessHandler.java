package com.habeshabite.backend.security;

import com.habeshabite.backend.entity.User;
import com.habeshabite.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth2/callback}") String redirectUri) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redirectUri = redirectUri;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        try {
            if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }

            String email = oAuth2User.getAttribute("email");
            if (!StringUtils.hasText(email)) {
                logger.error("Email not provided by OAuth2 provider");
                String errorUrl = buildRedirectUrlWithError("Email not provided by provider");
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
                return;
            }

            // Ensure user exists (in case CustomOAuth2UserService didn't create it)
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createUserFromOAuth2(oAuth2User, email));

            if (user == null) {
                logger.error("Failed to create or retrieve user for email: {}", email);
                String errorUrl = buildRedirectUrlWithError("Failed to process user account");
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
                return;
            }

            String token = jwtUtil.generateToken(user);
            @SuppressWarnings("null")
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", token)
                    .build()
                    .toUriString();
            Objects.requireNonNull(targetUrl, "Failed to build redirect URL");

            logger.info("OAuth2 login successful for user: {}", email);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            logger.error("Error during OAuth2 authentication success handling", e);
            String errorUrl = buildRedirectUrlWithError("Authentication failed: " + e.getMessage());
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

    private User createUserFromOAuth2(OAuth2User oAuth2User, String email) {
        try {
            String fullName = oAuth2User.getAttribute("name");
            if (!StringUtils.hasText(fullName)) {
                fullName = oAuth2User.getAttribute("given_name");
            }
            if (!StringUtils.hasText(fullName)) {
                fullName = email;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(User.Role.USER);
            user.setPhone("PENDING");
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            
            logger.info("Creating new user from OAuth2: {}", email);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to create user from OAuth2", e);
            return null;
        }
    }

    @SuppressWarnings("null")
    private String buildRedirectUrlWithError(String message) {
        URI uri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", message)
                .build()
                .toUri();
        String result = uri.toString();
        Objects.requireNonNull(result, "Failed to build redirect URL");
        return result;
    }
}
