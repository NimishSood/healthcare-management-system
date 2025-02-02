package com.example.healthcare.config;

import com.example.healthcare.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF for testing. In production, CSRF protection is highly recommended.
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints for authentication (registration and login)
//                        .requestMatchers("/api/auth/**").permitAll()
//                        // Endpoints secured for admins only. (Assumes your UserDetailsService returns roles with the "ROLE_" prefix.)
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        // Endpoints secured for owners only.
//                        .requestMatchers("/owner/**").hasRole("OWNER")
//                        // Doctors endpoints: accessible to DOCTOR, ADMIN, and OWNER
//                        .requestMatchers("/doctors/**").hasAnyRole("DOCTOR", "ADMIN", "OWNER")
//                        // Patients endpoints: accessible to PATIENT, ADMIN, and OWNER
//                        .requestMatchers("/patients/**").hasAnyRole("PATIENT", "ADMIN", "OWNER")
//                        // Notifications: any authenticated user can access their own notifications.
//                        .requestMatchers("/notifications/**").authenticated()
//                        // Any other endpoints require authentication.
//                        .anyRequest().authenticated()
//                )
//                // Use stateless sessions for a REST API (JWT or basic auth can be used).
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // For testing, we’ll use HTTP Basic Authentication.
//                .httpBasic();
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // ✅ Ensure it matches the DB role
                        .anyRequest().authenticated()
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
