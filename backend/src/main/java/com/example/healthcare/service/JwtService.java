package com.example.healthcare.service;

import com.example.healthcare.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service responsible for handling JWT (JSON Web Token) operations,
 * including token generation, validation, and claim extraction.
 * <p>
 * Used for stateless authentication across the Healthcare Management System.
 */
@Service
public class JwtService {

    /**
     * Secret key used to sign and validate JWT tokens.
     * Loaded from application.properties.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Token expiration time in milliseconds.
     * Currently set to 1 day (86400000 ms).
     */
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    /**
     * Generates the signing key from the configured secret key.
     *
     * @return Key object for signing/verifying JWT tokens
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a JWT token for an authenticated user.
     * The token includes the user's email as subject and role as a claim.
     *
     * @param user The authenticated user for whom the token is generated
     * @return Signed JWT token as a String
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token to ensure it is properly signed and not expired.
     *
     * @param token JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the username (email) from the JWT token.
     *
     * @param token JWT token
     * @return Email (subject) stored in the token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the user role from the JWT token.
     *
     * @param token JWT token
     * @return Role value stored in the token
     */
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}
