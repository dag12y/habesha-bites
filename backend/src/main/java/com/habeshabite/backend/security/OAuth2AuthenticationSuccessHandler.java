package com.habeshabite.backend.security;

import com.habeshabite.backend.entity.User;
import com.habeshabite.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
            UserRepository userRepository,
            @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth2/callback}") String redirectUri) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String email = oAuth2User.getAttribute("email");
        if (!StringUtils.hasText(email)) {
            String errorUrl = buildRedirectUrlWithError("Email not provided by provider");
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth2 login"));

        String token = jwtUtil.generateToken(user);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String buildRedirectUrlWithError(String message) {
        URI uri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", message)
                .build()
                .toUri();
        return uri.toString();
    }
}
