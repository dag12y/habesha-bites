package com.habeshabite.backend.config;

import com.habeshabite.backend.security.CustomOAuth2UserService;
import com.habeshabite.backend.security.JwtFilter;
import com.habeshabite.backend.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public SecurityConfig(JwtFilter jwtFilter,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
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
        source.registerCorsConfiguration("/oauth2/**", configuration);
        source.registerCorsConfiguration("/login/**", configuration);
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
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // allow all auth endpoints
                        .requestMatchers(HttpMethod.GET, "/api/foods/**").permitAll() // allow public access to view
                                                                                      // foods
                        .requestMatchers(HttpMethod.POST, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/tables").permitAll() // allow public access to view tables
                        .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated() // authenticated users can create bookings
                        .requestMatchers(HttpMethod.GET, "/api/bookings/my-bookings").authenticated() // users can view their own bookings
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").authenticated() // users can cancel their own bookings
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasAuthority("ADMIN") // admin only for all bookings
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
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/assign-driver").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/unassign-driver").hasAuthority("ADMIN")
                        .requestMatchers("/api/drivers/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated() // authenticated users can
                                                                                          // view profiles
                        .anyRequest().authenticated()) // everything else requires authentication
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            try {
                                String errorMessage = exception.getMessage();
                                String redirectUri = "http://localhost:3000/oauth2/callback?error=" + 
                                    java.net.URLEncoder.encode(errorMessage != null ? errorMessage : "OAuth2 authentication failed", "UTF-8");
                                response.sendRedirect(redirectUri);
                            } catch (Exception e) {
                                response.sendError(500, "OAuth2 error handling failed");
                            }
                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
