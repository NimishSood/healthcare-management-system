package com.example.healthcare.security;

import com.example.healthcare.service.JwtService;
import com.example.healthcare.entity.User;
import com.example.healthcare.repository.UserRepository;
import com.example.healthcare.entity.enums.AccountStatus;
import com.example.healthcare.service.AuditLogService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {
        String path = request.getServletPath();
        // Skip auth endpoints
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("⏳ Incoming request to: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("🚫 Missing or malformed Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT expired");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // If not already authenticated, validate user and token
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Only consider non-deleted users
            User user = userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElse(null);
            if (user == null) {
                auditLogService.logAction(
                        "Unauthorized Request", email, "UNKNOWN",
                        "Access denied", "Reason: Invalid token or user deleted", null
                );
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or user not found");
                return;
            }

            if (user.getAccountStatus() == AccountStatus.DEACTIVATED) {
                auditLogService.logAction(
                        "Blocked Request", user.getEmail(), user.getRole().name(),
                        "Access denied", "Reason: Account deactivated", null
                );
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account has been deactivated");
                return;
            }

            if (jwtService.validateToken(token)) {
                System.out.println("✅ Setting authentication for: " + email);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                auditLogService.logAction(
                        "Unauthorized Request", email, user.getRole().name(),
                        "Access denied", "Reason: Token validation failed", null
                );
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
