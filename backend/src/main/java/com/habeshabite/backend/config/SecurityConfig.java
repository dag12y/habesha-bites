package com.habeshabite.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for testing APIs
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll() // Allow all /auth endpoints
                .anyRequest().authenticated() // Everything else requires auth
                .and()
                .httpBasic(); // Optional: use basic auth for testing other endpoints

        return http.build();
    }
}
