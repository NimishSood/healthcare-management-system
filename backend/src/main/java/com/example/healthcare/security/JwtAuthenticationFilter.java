package com.example.healthcare.security;

import com.example.healthcare.entity.User;
import com.example.healthcare.entity.enums.AccountStatus;
import com.example.healthcare.repository.UserRepository;
import com.example.healthcare.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1) Skip auth endpoints
        if (request.getServletPath().startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extract and validate JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("AUTH FILTER: Raw JWT: " + token);

        String email;
        try {
            email = jwtService.extractUsername(token);
            System.out.println("AUTH FILTER: Extracted email from JWT: " + email);
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT expired");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // 3) Load user, block if deleted or deactivated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmailAndIsDeletedFalse(email).orElse(null);
            System.out.println("AUTH FILTER: user found? " + (user != null ? user.getId() : "NOT FOUND"));

            if (user != null
                    && user.getAccountStatus() != AccountStatus.DEACTIVATED
                    && jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                // **DEBUG PRINT: LOG THE AUTHORITIES**
                System.out.println(">>> AUTHORITIES FOR " + user.getEmail() + ":");
                if (user.getAuthorities() != null) {
                    for (var ga : user.getAuthorities()) {
                        System.out.println("    - " + ga.getAuthority());
                    }
                } else {
                    System.out.println("    - (No authorities!)");
                }

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }


}
