package com.habeshabite.backend.config;

import com.habeshabite.backend.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // disable CSRF for POST requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow all OPTIONS requests for CORS
                                                                                // preflight
                        .requestMatchers("/api/auth/**").permitAll() // allow all auth endpoints
                        .requestMatchers(HttpMethod.GET, "/api/foods/**").permitAll() // allow public access to view
                                                                                      // foods
                        .requestMatchers(HttpMethod.POST, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated() // authenticated users can
                                                                                         // create orders
                        .requestMatchers(HttpMethod.GET, "/api/orders/my-orders").authenticated() // users can view
                                                                                                  // their own orders
                        .requestMatchers(HttpMethod.GET, "/api/orders/*").authenticated() // users can view their own
                                                                                          // order
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/cancel").authenticated() // users can cancel
                                                                                                 // their own orders
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasAuthority("ADMIN") // admin only for all
                                                                                              // orders
                        .requestMatchers(HttpMethod.GET, "/api/orders/status/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated() // authenticated users can
                                                                                          // view profiles
                        .anyRequest().authenticated()) // everything else requires authentication
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
