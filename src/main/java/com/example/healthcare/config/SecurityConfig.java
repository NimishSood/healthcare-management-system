package com.example.healthcare.config;

import com.example.healthcare.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Public Endpoints (Login/Register)
                        .requestMatchers("/owner/**").hasAuthority("ROLE_OWNER")  // Only Owners
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // Only Admins
                        .requestMatchers("/doctor/**").hasAuthority("ROLE_DOCTOR")  // Only Doctors
                        .anyRequest().authenticated()  // All other endpoints require authentication
                )
                .userDetailsService(userDetailsService)
                .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
